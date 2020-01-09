package hit.internetopo.controller;

import hit.internetopo.domain.IPNodeEntity;
import hit.internetopo.repositories.IPNodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nodes")
public class IPNodeController {

    private final IPNodeRepository ipNodeRepository;

    public IPNodeController(IPNodeRepository ipNodeRepository) {
        this.ipNodeRepository = ipNodeRepository;
    }

    @PutMapping
    IPNodeEntity createOrUpdateIPNode(@RequestBody IPNodeEntity newNode) {
        return ipNodeRepository.save(newNode);
    }

    @GetMapping(value = { "", "/" })
    List<IPNodeEntity> getIPNodes() {
        return (List<IPNodeEntity>) ipNodeRepository.findAll();
    }

    @GetMapping("/by-ip")
    IPNodeEntity byIP(@RequestParam String ip) {
        return ipNodeRepository.findOneByIp(ip).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        ipNodeRepository.deleteById(id);
    }
}
