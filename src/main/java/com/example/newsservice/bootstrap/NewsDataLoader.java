package com.example.newsservice.bootstrap;

import com.example.newsservice.entity.News;
import com.example.newsservice.entity.ReadStatus;
import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.repository.ReadStatusRepository;
import com.example.newsservice.repository.UserRepository;
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

    @Override
    public void run(String... args) throws Exception {
        loadNewsData();
    }

    protected void loadNewsData() {

        ReadStatus readStatus = readStatusRepository.save(ReadStatus.builder()
                .accountId(userRepository.getUserAccountId("john"))
                .readDate(null)
                .build()
        );

        newsRepository.save(News.builder()
                .title("Princes Diana got divorced with prince Charles")
                .text("Princes Diana got divorced with prince Charles and had begun an affair with billionair's son Dodi " +
                        "Al Fayet. She continues to take care of ill and poor children in Africa.")
                .linkToPhoto("https://metro.co.uk/wp-content/uploads/2022/08/SEC_121997629-5526.jpg")
                .validFrom(OffsetDateTime.parse("2022-07-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2022-08-29T02:05:25+02:00"))
                .allowedRole("PUBLISHER")
                .unAllowedRole("READER")
                .readStatus(readStatus)
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
                .readStatus(readStatus)
                .build());

        newsRepository.save(News.builder()
                .title("New Metallica Single")
                .text("A new Metallica Single has been released on 23th of June 2003. It is called St. Anger \n" +
                        " and the video to it was shot in a maximum security federal prison.\n")
                .linkToPhoto("https://en.wikipedia.org/wiki/St._Anger_(song)#/media/File:Metallica_-_St._Anger_single_cover.jpg")
                .validFrom(OffsetDateTime.parse("2003-06-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2003-09-29T02:05:25+02:00"))
                .allowedRole("READER")
                .unAllowedRole("PUBLISHER")
                .readStatus(readStatus)
                .build());

        log.debug("News Loaded: " + newsRepository.count());
    }
}
