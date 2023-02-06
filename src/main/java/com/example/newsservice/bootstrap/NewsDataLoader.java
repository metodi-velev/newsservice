package com.example.newsservice.bootstrap;

import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.entity.ReadStatus;
import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.repository.PhotoRepository;
import com.example.newsservice.repository.ReadStatusRepository;
import com.example.newsservice.repository.UserRepository;
import com.example.newsservice.utils.PhotoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Order(2)
@Component
public class NewsDataLoader implements CommandLineRunner {

    private final NewsRepository newsRepository;

    private final ReadStatusRepository readStatusRepository;

    private final UserRepository userRepository;

    private final PhotoRepository photoRepository;

    private final PhotoUtils photoUtils;

    @Override
    public void run(String... args) throws Exception {
        loadNewsData();
    }

    protected void loadNewsData() throws IOException, ImageReadException {

        Photo photo1 = photoRepository.save(Photo.builder()
                .photoName("Princess Diana")
                .metaData(photoUtils.getMetadataFromPhoto("https://metro.co.uk/wp-content/uploads/2022/08/SEC_121997629-5526.jpg"))
                .photoData(new byte[]{})
                .linkToPhoto("https://metro.co.uk/wp-content/uploads/2022/08/SEC_121997629-5526.jpg")
                .build()
        );

        Photo photo2 = photoRepository.save(Photo.builder()
                .photoName("Harry Potter Book")
                .metaData(photoUtils.getMetadataFromPhoto("https://thumbs.dreamstime.com/z/harry-potter-warner-brothers-studio-tour-london-uk-entrance-where-filmed-actual-film-series-movie-poster-sorcerers-164168768.jpg"))
                .photoData(new byte[]{})
                .linkToPhoto("https://thumbs.dreamstime.com/z/harry-potter-warner-brothers-studio-tour-london-uk-entrance-where-filmed-actual-film-series-movie-poster-sorcerers-164168768.jpg")
                .build()
        );

        Photo photo3 = photoRepository.save(Photo.builder()
                .photoName("Metallica Single St. Anger")
                .metaData(photoUtils.getMetadataFromPhoto("https://1.bp.blogspot.com/-ERAaZVGM2Og/V_6AofQVhUI/AAAAAAAAECw/fxwjRlATA3gVdqhBIi6Ce6kWZTkpBx3mACLcB/s1600/metallica%2Bpic%2Bst%2Banger%2Bera.jpg"))
                .photoData(new byte[]{})
                .linkToPhoto("https://1.bp.blogspot.com/-ERAaZVGM2Og/V_6AofQVhUI/AAAAAAAAECw/fxwjRlATA3gVdqhBIi6Ce6kWZTkpBx3mACLcB/s1600/metallica%2Bpic%2Bst%2Banger%2Bera.jpg")
                .build()
        );

        ReadStatus readStatus1 = readStatusRepository.save(ReadStatus.builder()
                .accountId(userRepository.getUserAccountId("john"))
                .readDate(null)
                .build()
        );

        ReadStatus readStatus2 = readStatusRepository.save(ReadStatus.builder()
                .accountId(userRepository.getUserAccountId("john"))
                .readDate(null)
                .build()
        );

        ReadStatus readStatus3 = readStatusRepository.save(ReadStatus.builder()
                .accountId(userRepository.getUserAccountId("john"))
                .readDate(null)
                .build()
        );

        newsRepository.save(News.builder()
                .title("Princess Diana got divorced with prince Charles")
                .text("Princess Diana got divorced with prince Charles and had begun an affair with billionair's son Dodi " +
                        "Al Fayet. She continues to take care of ill and poor children in Africa.")
                .linkToPhoto("https://metro.co.uk/wp-content/uploads/2022/08/SEC_121997629-5526.jpg")
                .validFrom(OffsetDateTime.parse("2022-07-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2022-08-29T02:05:25+02:00"))
                .allowedRole("PUBLISHER")
                .unAllowedRole("READER")
                .readStatus(readStatus1)
                .photo(photo1)
                .build());

        newsRepository.save(News.builder()
                .title("New Harry Potter Book")
                .text("A new Harry Potter book has been released on 9th of June 2010. In the book the main actor \n" +
                        " punishes the evil creatures with a spell.\n")
                .linkToPhoto("https://thumbs.dreamstime.com/z/harry-potter-warner-brothers-studio-tour-london-uk-entrance-where-filmed-actual-film-series-movie-poster-sorcerers-164168768.jpg")
                .validFrom(OffsetDateTime.parse("2020-03-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2020-06-29T02:05:25+02:00"))
                .allowedRole("READER")
                .unAllowedRole("PUBLISHER")
                .readStatus(readStatus2)
                .photo(photo2)
                .build());

        newsRepository.save(News.builder()
                .title("New Metallica Single")
                .text("A new Metallica Single has been released on 23th of June 2003. It is called St. Anger \n" +
                        " and the video to it was shot in a maximum security federal prison.\n")
                .linkToPhoto("https://1.bp.blogspot.com/-ERAaZVGM2Og/V_6AofQVhUI/AAAAAAAAECw/fxwjRlATA3gVdqhBIi6Ce6kWZTkpBx3mACLcB/s1600/metallica%2Bpic%2Bst%2Banger%2Bera.jpg")
                .validFrom(OffsetDateTime.parse("2003-06-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2003-09-29T02:05:25+02:00"))
                .allowedRole("READER")
                .unAllowedRole("PUBLISHER")
                .readStatus(readStatus3)
                .photo(photo3)
                .build());

        log.debug("News Loaded: " + newsRepository.count());
        log.debug("Photos Loaded: " + photoRepository.count());
    }
}
