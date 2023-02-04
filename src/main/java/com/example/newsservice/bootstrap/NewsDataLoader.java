package com.example.newsservice.bootstrap;

import com.example.newsservice.entity.News;
import com.example.newsservice.entity.Photo;
import com.example.newsservice.entity.ReadStatus;
import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.repository.PhotoRepository;
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

    private final PhotoRepository photoRepository;

    @Override
    public void run(String... args) throws Exception {
        loadNewsData();
    }

    protected void loadNewsData() {

        Photo photo1 = photoRepository.save(Photo.builder()
                .photoName("Princess Diana")
                .metaData("Metadata to Princess Diana's Photo")
                .photoData(new byte[]{})
                .linkToPhoto("https://metro.co.uk/wp-content/uploads/2022/08/SEC_121997629-5526.jpg")
                .build()
        );

        Photo photo2 = photoRepository.save(Photo.builder()
                .photoName("Harry Potter Book")
                .metaData("Metadata to Harry Potter Book")
                .photoData(new byte[]{})
                .linkToPhoto("https://thumbs.dreamstime.com/z/harry-potter-warner-brothers-studio-tour-london-uk-entrance-where-filmed-actual-film-series-movie-poster-sorcerers-164168768.jpg")
                .build()
        );

        Photo photo3 = photoRepository.save(Photo.builder()
                .photoName("Metallica Single St. Anger")
                .metaData("Metadata to Metallica Single St. Anger")
                .photoData(new byte[]{})
                .linkToPhoto("https://en.wikipedia.org/wiki/St._Anger_(song)#/media/File:Metallica_-_St._Anger_single_cover.jpg")
                .build()
        );

        ReadStatus readStatus = readStatusRepository.save(ReadStatus.builder()
                .accountId(userRepository.getUserAccountId("john"))
                .readDate(null)
                .build()
        );

        News news1 = newsRepository.save(News.builder()
                .title("Princess Diana got divorced with prince Charles")
                .text("Princess Diana got divorced with prince Charles and had begun an affair with billionair's son Dodi " +
                        "Al Fayet. She continues to take care of ill and poor children in Africa.")
                .linkToPhoto("https://metro.co.uk/wp-content/uploads/2022/08/SEC_121997629-5526.jpg")
                .validFrom(OffsetDateTime.parse("2022-07-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2022-08-29T02:05:25+02:00"))
                .allowedRole("PUBLISHER")
                .unAllowedRole("READER")
                .readStatus(readStatus)
                .photo(photo1)
                .build());

        News news2 = newsRepository.save(News.builder()
                .title("New Harry Potter Book")
                .text("A new Harry Potter book has been released on 9th of June 2010. In the book the main actor \n" +
                        " punishes the evil creatures with a spell.\n")
                .linkToPhoto("https://thumbs.dreamstime.com/z/harry-potter-warner-brothers-studio-tour-london-uk-entrance-where-filmed-actual-film-series-movie-poster-sorcerers-164168768.jpg")
                .validFrom(OffsetDateTime.parse("2020-03-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2020-06-29T02:05:25+02:00"))
                .allowedRole("READER")
                .unAllowedRole("PUBLISHER")
                .readStatus(readStatus)
                .photo(photo2)
                .build());

        News news3 = newsRepository.save(News.builder()
                .title("New Metallica Single")
                .text("A new Metallica Single has been released on 23th of June 2003. It is called St. Anger \n" +
                        " and the video to it was shot in a maximum security federal prison.\n")
                .linkToPhoto("https://en.wikipedia.org/wiki/St._Anger_(song)#/media/File:Metallica_-_St._Anger_single_cover.jpg")
                .validFrom(OffsetDateTime.parse("2003-06-29T02:05:25+02:00"))
                .validTo(OffsetDateTime.parse("2003-09-29T02:05:25+02:00"))
                .allowedRole("READER")
                .unAllowedRole("PUBLISHER")
                .readStatus(readStatus)
                .photo(photo3)
                .build());

        photo1.setNews(news1);
        photo2.setNews(news2);
        photo3.setNews(news3);

        photoRepository.save(photo1);
        photoRepository.save(photo2);
        photoRepository.save(photo3);

        log.debug("News Loaded: " + newsRepository.count());
        log.debug("Photos Loaded: " + photoRepository.count());
    }
}
