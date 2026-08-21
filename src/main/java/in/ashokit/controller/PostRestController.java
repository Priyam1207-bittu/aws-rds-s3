package in.ashokit.controller;

import in.ashokit.entity.Post;
import in.ashokit.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PostRestController {
    private final PostService service;

    public PostRestController(PostService service) {
        this.service = service;
    }

    @PostMapping(value = "/post", consumes = "multipart/form-data")
    public ResponseEntity<Post> create(@RequestParam("text")String text,
                                       @RequestParam("image") MultipartFile image) {
        try {
            Post post = service.createPost(text, image);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
