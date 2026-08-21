package in.ashokit.service;

import in.ashokit.entity.Post;
import in.ashokit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class PostService {

    private final S3Client s3Client;
    private final PostRepository postRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public PostService(
            S3Client s3Client,
            PostRepository postRepository) {

        this.s3Client = s3Client;
        this.postRepository = postRepository;
    }

    public Post createPost(
            String text,
            MultipartFile image) throws IOException {

        // 1. Generate unique S3 object name
        String fileName = image.getOriginalFilename();

        String objectKey = "posts/" + fileName;

        String objectUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + objectKey;

        // 2. Create S3 request
        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType(image.getContentType())
                        .build();

        // 3. Upload image to S3
        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(
                        image.getInputStream(),
                        image.getSize()
                )
        );

        // 4. Save text + S3 objectUrl in RDS
        Post post = new Post();

        post.setText(text);
        post.setImageKey(objectUrl);

        return postRepository.save(post);
    }
}
