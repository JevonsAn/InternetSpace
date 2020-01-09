# -*- coding:utf-8 -*-
import os
import argparse

def analyze_all(filenames):
    dirname = filenames[0].strip(filenames[0].split("/")[-1])
    print(dirname)
    if not dirname:
        dirname = "."
    fs = filter(lambda l: l.endswith("warts.gz"), filenames)
    all_num = len(fs)
    if not all_num:
        raise ValueError("文件夹中不包含warts文件")
    for i, f in enumerate(fs):
        os.system("/root/vtopo/scripts/analyze warts2link %s" % (f))
        print("%.1f%%" % (i / all_num))


def merge_all(filenames):
    dirname = filenames[0].strip(filenames[0].split("/")[-1])
    if not dirname:
        dirname = "."
    fs = filter(lambda l: l.endswith("links"), filenames)
    all_num = len(fs)
    if not all_num:
        raise ValueError("文件夹中不包含.links文件")
    result_filename = "%s/%s_merge.csv" % (dirname.rstrip("/"),
                                             os.path.split(dirname.rstrip("/"))[-1])
    comd = "perl /root/vtopo/scripts/linkmerge.pl %s" % (" ".join([f for f in fs]))
    comd += " >  %s" % result_filename
    # print(comd)
    os.system(comd)
    return result_filename

def import_into_neo4j(filename):
    os.system("python import_neo4j.py %s") % filename

parser = argparse.ArgumentParser(description=
    "处理某文件夹下的所有warts文件。\n使用方法为python analyze_all.py -d dirname [-a -m -i]")
group = parser.add_mutually_exclusive_group()
group.add_argument('--files', '-f', help="要处理的（一个或多个）文件", nargs='+')
group.add_argument("--dir", "-d", help="要处理的目标文件夹")
parser.add_argument("--merge", '-m', help="进行merge", action="store_true")
parser.add_argument("--analyze", '-a', help="进行analyze", action="store_true")
parser.add_argument("--imports", '-i', help="导入neo4j", action="store_true")
args = parser.parse_args()

if __name__ == "__main__":
    try:
        files = []
        if args.files:
            files = args.files
        if args.dir:
            files = [args.dir.rstrip("/") + "/" + f for f in os.listdir(args.dir)]
            # print(files[:5])
        if not files:
            raise ValueError("缺失参数")
        if args.analyze:
            analyze_all(files)
        if args.merge:
            file_name = merge_all(files)
        if args.imports:
            import_into_neo4j(file_name)
    except Exception as e:
        print(e)
    
