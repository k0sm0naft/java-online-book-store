package ua.bookstore.online.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.bookstore.online.exception.SpecificationProviderNotFoundException;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.Parameter;
import ua.bookstore.online.repository.SpecificationProvider;
import ua.bookstore.online.repository.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(Parameter parameter) {
        return bookSpecificationProviders
                .stream()
                .filter(p -> p.getParameter() == parameter)
                .findFirst()
                .orElseThrow(() -> new SpecificationProviderNotFoundException(
                        "Can't find correct specification provider or parameter " + parameter));
    }
}
