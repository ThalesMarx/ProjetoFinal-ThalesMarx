package com.exemplo.ewallet.e2e;

import com.exemplo.ewallet.model.Conta;
import com.exemplo.ewallet.repository.ContaRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DepositoSeleniumTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ContaRepository contaRepository;

    private WebDriver driver;
    private Conta contaTeste;

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
        contaTeste = contaRepository.save(new Conta(null, "Usuario Selenium", 100.0));

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void deveRealizarDepositoPelaInterfaceWeb() {
        driver.get("http://localhost:" + port + "/index.html");

        WebElement inputId = driver.findElement(By.id("contaId"));
        inputId.clear();
        inputId.sendKeys(contaTeste.getId().toString());

        WebElement inputValor = driver.findElement(By.id("valor"));
        inputValor.sendKeys("250");

        driver.findElement(By.id("btnDepositar")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement resultado = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("resultado"))
        );

        wait.until(ExpectedConditions.textToBePresentInElement(resultado, "350.00"));
        assertTrue(resultado.getText().contains("Depósito realizado! Novo saldo: R$ 350.00"));
    }
}
