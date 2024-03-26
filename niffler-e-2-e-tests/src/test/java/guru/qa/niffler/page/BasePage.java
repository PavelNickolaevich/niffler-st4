package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.message.Msg;
import guru.qa.niffler.pageobject.fragment.HeaderFragment;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public abstract class BasePage<T extends BasePage> {

  protected static final Config CFG = Config.getInstance();

  protected final SelenideElement toaster = $(".Toastify__toast-body");
  private final SelenideElement mainBtn = $x("//li[@data-tooltip-id='main']");
  private final SelenideElement friendsBtn = $x("//li[@data-tooltip-id='friends']");
  private final SelenideElement peopleBtn = $x("//li[@data-tooltip-id='people']");
  private final SelenideElement profileBtn = $x("//li[@data-tooltip-id='profile']");

  public abstract T waitForPageLoaded();

  @Step("Check that success message appears: {msg}")
  @SuppressWarnings("unchecked")
  public T checkToasterMessage(Msg msg) {
    toaster.should(Condition.visible).should(text(msg.getMessage()));
    return (T) this;
  }

  @Step("Перейти в Main Page")
  public void clickMainBtm() {
    mainBtn.click();
  }

  @Step("Перейти в Friends")
  public void clickFriendsBtn() {
    friendsBtn.click();
  }

  @Step("Перейти в All People")
  public void clickAllPeopleBtn() {
    peopleBtn.click();
  }

  @Step("Перейти в Profile")
  public void clickProfileBtn() {
    profileBtn.click();
  }

}
