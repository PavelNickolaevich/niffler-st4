package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ElementWithTextNotFound;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.niffler.model.SpendJson;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SpendCollectionCondition {

    public static CollectionCondition spends(SpendJson... expectedSPends) {
        return new CollectionCondition() {

            private List<String> expectedList = new ArrayList<>();

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {

                List<String> actualList = new ArrayList<>();

                if (elements.size() != expectedSPends.length) {
                    return CheckResult.rejected("Incorrect table size", elements);
                }

                for (WebElement element : elements) {

                    List<String> tds = element.findElements(By.cssSelector("td")).stream()
                            .map(el -> el.getText())
                            .filter(value -> !value.equals(""))
                            .collect(Collectors.toList());

                    actualList.addAll(tds);
                }

                    for (SpendJson expectedSPend : expectedSPends) {

                        expectedList.add(new SimpleDateFormat("dd MMM yy", Locale.ENGLISH).format(expectedSPend.spendDate()));
                        expectedList.add(String.valueOf(expectedSPend.amount().intValue()));
                        expectedList.add(expectedSPend.currency().name());
                        expectedList.add(expectedSPend.category());
                        expectedList.add(expectedSPend.description());
                    }

                    if (actualList.equals(expectedList)) {
                        return CheckResult.accepted();
                    }
                return CheckResult.rejected("Incorrect spends content", actualList);
            }


            @SneakyThrows
            @Override
            public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {

                List<String> actualData = lastCheckResult.getActualValue();
                List<String> wrongResults = expectedList.stream().filter(i -> !actualData.contains(i))
                        .collect(Collectors.toList());

                throw new ElementWithTextNotFound(collection, wrongResults , lastCheckResult.getActualValue(), explanation, timeoutMs, cause);

            }

            @Override
            public boolean missingElementSatisfiesCondition() {
                return false;
            }
        };
    }
}
