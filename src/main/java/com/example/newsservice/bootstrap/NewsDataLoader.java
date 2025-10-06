package com.example.newsservice.bootstrap;

import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.entity.ReadStatus;
import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.repository.PhotoRepository;
import com.example.newsservice.repository.ReadStatusRepository;
import com.example.newsservice.repository.UserRepository;
import com.example.newsservice.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    private final PhotoService photoService;

    @Override
    public void run(String... args) {
        loadNewsData();
    }

    protected void loadNewsData() {

        if (newsRepository.count() > 0) {
            log.info("News Data Already Loaded");
            return;
        }

        Photo photo1 = photoService.getOrCreatePhoto(
                "Breaking News Photo",
                "https://metro.co.uk/wp-content/uploads/2022/08/SEC_121997629-5526.jpg"
        );

        Photo photo2 = photoService.getOrCreatePhoto(
                "Harry Potter Book",
                "https://thumbs.dreamstime.com/z/harry-potter-warner-brothers-studio-tour-london-uk-entrance-where-filmed-actual-film-series-movie-poster-sorcerers-164168768.jpg"
        );

        Photo photo3 = photoService.getOrCreatePhoto(
                "Metallica Single St. Anger",
                "https://1.bp.blogspot.com/-ERAaZVGM2Og/V_6AofQVhUI/AAAAAAAAECw/fxwjRlATA3gVdqhBIi6Ce6kWZTkpBx3mACLcB/s1600/metallica%2Bpic%2Bst%2Banger%2Bera.jpg"
        );

        ReadStatus readStatus1 = readStatusRepository.save(ReadStatus.builder()
                .accountId(userRepository.getUserAccountId("john"))
                .readDate(null)
                .build()
        );

        ReadStatus readStatus2 = readStatusRepository.save(ReadStatus.builder()
                .accountId(userRepository.getUserAccountId("martin"))
                .readDate(null)
                .build()
        );

        ReadStatus readStatus3 = readStatusRepository.save(ReadStatus.builder()
                .accountId(userRepository.getUserAccountId("lisa"))
                .readDate(null)
                .build()
        );

        createNewsIfNotFound(
                ("Princess Diana got divorced with prince Charles"),
                ("""
                          Princess Diana got divorced with prince Charles and had begun an affair with billionair's son Dodi
                          Al Fayet. She continues to take care of ill and poor children in Africa.
                        """
                ),
                ("https://metro.co.uk/wp-content/uploads/2022/08/SEC_121997629-5526.jpg"),
                (OffsetDateTime.parse("2022-07-29T02:05:25+02:00")),
                (OffsetDateTime.parse("2022-08-29T02:05:25+02:00")),
                ("PUBLISHER"),
                ("READER"),
                (readStatus1),
                (photo1));

        createNewsIfNotFound(
                ("New Harry Potter Book"),
                ("""
                          A new Harry Potter book has been released on 9th of June 2010. In the book the main actor
                          punishes the evil creatures with a spell.
                        """
                ),
                ("https://thumbs.dreamstime.com/z/harry-potter-warner-brothers-studio-tour-london-uk-entrance-where-filmed-actual-film-series-movie-poster-sorcerers-164168768.jpg"),
                (OffsetDateTime.parse("2020-03-29T02:05:25+02:00")),
                (OffsetDateTime.parse("2020-06-29T02:05:25+02:00")),
                ("READER"),
                ("PUBLISHER"),
                (readStatus2),
                (photo2)
        );

        createNewsIfNotFound(
                ("New Metallica Single"),
                ("""
                          A new Metallica Single has been released on 23th of June 2003. It is called St. Anger
                          and the video to it was shot in a maximum security federal prison.
                        """
                ),
                ("https://1.bp.blogspot.com/-ERAaZVGM2Og/V_6AofQVhUI/AAAAAAAAECw/fxwjRlATA3gVdqhBIi6Ce6kWZTkpBx3mACLcB/s1600/metallica%2Bpic%2Bst%2Banger%2Bera.jpg"),
                (OffsetDateTime.parse("2003-06-29T02:05:25+02:00")),
                (OffsetDateTime.parse("2003-09-29T02:05:25+02:00")),
                ("READER"),
                ("PUBLISHER"),
                (readStatus3),
                (photo3)
        );

        log.debug("News Loaded: " + newsRepository.count());
        log.debug("Photos Loaded: " + photoRepository.count());
    }

    private void createNewsIfNotFound(String title, String text, String linkToPhoto, OffsetDateTime validFrom, OffsetDateTime validTo,
                                      String allowedRole, String unAllowedRole, ReadStatus readStatus, Photo photo) {
        newsRepository.findByLinkToPhoto(linkToPhoto).ifPresentOrElse(
                u -> {
                }
                ,
                () -> {

                    News news =
                            News.builder()
                                    .title(title)
                                    .text(text)
                                    .linkToPhoto(linkToPhoto)
                                    .validFrom(validFrom)
                                    .validTo(validTo)
                                    .allowedRole(allowedRole)
                                    .unAllowedRole(unAllowedRole)
                                    .readStatus(readStatus)
                                    .photo(photo)
                                    .build();

                    newsRepository.save(news);
                });
    }
}
