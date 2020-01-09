# -*- coding:utf-8 -*-
from neo4j import GraphDatabase
import pymysql
import json
import argparse
import os
import datetime
import time
import radix

current_dir = os.path.dirname(os.path.realpath(__file__))
setting_dir = os.path.join(*(list(os.path.split(current_dir)[:-1]) + ["setting", "database.json"]))

with open(setting_dir) as f:
    setting = json.loads(f.read())
    neo4j_setting = setting["neo4j"]
    NEO4J_URI = neo4j_setting["NEO4J_URI"]
    NEO4J_USER = neo4j_setting["NEO4J_USER"]
    NEO4J_PWD = neo4j_setting["NEO4J_PWD"]
    mysql_setting = setting["mysql"]["databases"]
    bgp_setting = mysql_setting["bgp"]
    BGP_HOST, BGP_USER, BGP_PWD, BGP_DB = bgp_setting["host"], bgp_setting["user"], bgp_setting["passwd"], bgp_setting["db"]


class Neo4jDriver(object):

    def __init__(self):
        self._driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PWD))
    
    def delete_node_edge(self, label):
        node_label = label + "_node_table"
        edge_label = label + "_edge_table"
        def delete_func(tx):
            result = tx.run("MATCH (: %s )-[e: %s] -> (: %s) DELETE e" % (node_label, edge_label, node_label))
            result = tx.run("MATCH (n: %s) DELETE n" % node_label)
            return result

        with self._driver.session() as session:
            session.write_transaction(delete_func)

    def create_index(self, label, property):
        node_label = label + "_node_table"
        def func(tx):
            tx.run("CREATE INDEX ON :%s(%s);" % (node_label, property))

        with self._driver.session() as session:
            session.write_transaction(func)

    def insert_node(self, data, label):
        with self._driver.session() as session:
            session.write_transaction(self._create_nodes, data, label + "_node_table")
    
    def insert_edge(self, data, label):
        with self._driver.session() as session:
            session.write_transaction(self._create_edges, data, label + "_node_table", label + "_edge_table")

    @staticmethod
    def _create_nodes(tx, data, label):
        # tx.run("{batch: %s}" % json.dumps(data))
        result = tx.run("UNWIND $batch as line\
                        MERGE(n: %s {ip: line[0]})\
                        ON CREATE SET n.is_dest = line[1], n.asn = line[2]\
                        ON MATCH SET n.is_dest = line[1], n.asn = line[2]" % label, batch=data)
        return result
    
    @staticmethod
    def _create_edges(tx, data, node_label, edge_label):
        # tx.run("{batch: %s}" % json.dumps(data))
        result = tx.run("UNWIND $batch as line\
                        MATCH (in: %s {ip: line[0]}) MATCH (out: %s {ip: line[1]})\
                        MERGE (in)-[e: %s]->(out)\
                        ON CREATE set\
                            e.is_dest=line[2],\
                            e.star=line[3],\
                            e.delay=line[4],\
                            e.monitor=line[5]" % (node_label, node_label, edge_label), batch=data)
        return result

    def execute_and_get_one(self, cql):
        with self._driver.session() as session:
            return session.read_transaction(self._get_one_by_cql, cql)

    @staticmethod
    def _get_one_by_cql(tx, cql):
        result = tx.run(cql)
        return result.single()[0]

    def execute_and_get_all(self, cql):
        with self._driver.session() as session:
            return session.read_transaction(self._get_all_by_cql, cql)

    @staticmethod
    def _get_all_by_cql(tx, cql):
        result = tx.run(cql)
        return result.data()
    
    def __del__(self):
        self.close()

    def close(self):
        if self._driver:
            self._driver.close()


def test_conn():
    print("开始测试neo4j连接")
    neo4j_conn = Neo4jDriver()
    result = neo4j_conn.execute_and_get_all(
        "MATCH (n:node) RETURN count(n) LIMIT 1")
    print(result)


def buildBGPPrefixTree():
    print("\nbuilding BGP prefix tree...")
    db = pymysql.connect(host=BGP_HOST, user=BGP_USER, passwd=BGP_PWD, db=BGP_DB)
    cur = db.cursor()
    cur.execute("select prefix, origin from originss")
    res = cur.fetchall()

    rtree = radix.Radix()
    for item in res:
        try:
            prefix = item[0]
            asn = int(item[1])
            rnode = rtree.add(prefix)
            rnode.data["asn"] = asn
        except:
            continue
    
    cur.close()
    db.close()
    print("building complete.")
    return rtree


bgp_rtree = buildBGPPrefixTree()

def markASN(ip):
    rnode = bgp_rtree.search_best(ip)
    asn = 0
    if rnode:
        asn = rnode.data["asn"]
    return asn

def import_from_file(filename):    
    neo4j_conn = Neo4jDriver()
    label = filename.split("/")[-1][:filename.split("/")[-1].rfind('.')]  # 用文件名做标签
    label = label.replace("-", "_").replace(".", "_")
    with open(filename) as f:  
        neo4j_conn.delete_node_edge(label)    # 删除原标签的所有点和边
        neo4j_conn.create_index(label, "ip")  # 对ip属性建立索引
        # print(datetime.datetime.now())        # 完成所有预处理工作
        n = 0
        node_batch = []
        edge_batch = []
        for line in f:
            sp = line.strip().split(" ")
            node_batch.append([sp[0], "N", markASN(sp[0])])
            node_batch.append([sp[1], sp[2], markASN(sp[1])])
            edge_batch.append([sp[0], sp[1], sp[2], int(sp[3]), float(sp[4]), sp[7]])
            if n % 5000 == 0:
                # print("开始导入第%s次" % (n // 5000))
                # start_time = time.time()
                neo4j_conn.insert_node(node_batch, label)
                neo4j_conn.insert_edge(edge_batch, label)
                # print("完成, 耗时：%s" % (time.time() - start_time))                
                node_batch = []
                edge_batch = []
            n += 1
    return n

parser = argparse.ArgumentParser(description="将links文件导入neo4j")
parser.add_argument("filename")
args = parser.parse_args()

if __name__ == '__main__':
    try:
        if not args.filename:
            print("文件名不能为空")
            exit(0)
        filename = args.filename
    except Exception as e:
        raise e
    
    try:
        test_conn()
    except Exception as e:
        print("neo4j连接配置出错")
        raise e

    print("开始导入：")
    print(datetime.datetime.now())
    start_time = time.time()
    lines = import_from_file(filename)
    print("全部导入完成, 总计%s条, 耗时：%s" % (lines, time.time() - start_time))
    print(datetime.datetime.now())
    