package guru.qa.niffler.service;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SpecificRequestDumperFilterTest {

    private static String pattern = "http://localhost:8080/test2";

    @Test
    void doFilterCorrectHttpServletRequestAndMatchPatternTest(@Mock HttpServletRequest request,
                                                              @Mock ServletResponse response,
                                                              @Mock FilterChain filterChain,
                                                              @Mock GenericFilter decorate) throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn(pattern);

        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate, pattern);
        specificRequestDumperFilter.doFilter(request, response, filterChain);

        verify(decorate, times(1)).doFilter(request, response, filterChain);
    }

    @Test
    void doFilterWrongInstanceHttpServletRequestTest(@Mock ServletRequest request,
                                                     @Mock ServletResponse response,
                                                     @Mock FilterChain filterChain,
                                                     @Mock GenericFilter decorate) throws ServletException, IOException {
        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate, pattern);
        specificRequestDumperFilter.doFilter(request, response, filterChain);

        verify(decorate, times(0)).doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterWhenUriNotMatchUrlPattern(@Mock HttpServletRequest request,
                                           @Mock ServletResponse response,
                                           @Mock FilterChain filterChain,
                                           @Mock GenericFilter decorate) throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn(pattern);
        String wrongPattern = "http://localhost:8080/test";

        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate, wrongPattern);
        specificRequestDumperFilter.doFilter(request, response, filterChain);

        verify(decorate, never()).doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void destroyCorrectTest(@Mock GenericFilter decorate) {
        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate, pattern);
        specificRequestDumperFilter.destroy();

        verify(decorate, times(1)).destroy();
    }

}