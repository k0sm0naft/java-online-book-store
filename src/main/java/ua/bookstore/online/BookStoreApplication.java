package ua.bookstore.online;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.service.BookService;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book firstBook = new Book();
            firstBook.setTitle("First book");
            firstBook.setAuthor("First Author");
            firstBook.setDescription("First book in store");
            firstBook.setIsbn("123");
            firstBook.setPrice(BigDecimal.TEN);

            System.out.println(bookService.save(firstBook));
            System.out.println(bookService.findAll());
        };
    }
}
