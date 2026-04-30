package com.mykhailo.ticket_search.config;

import com.mykhailo.ticket_search.model.TicketEntity;
import com.mykhailo.ticket_search.repository.TicketJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TicketJpaRepository ticketJpaRepository;

    public DataInitializer(TicketJpaRepository ticketJpaRepository) {
        this.ticketJpaRepository = ticketJpaRepository;
    }

    @Override
    public void run(String... args) {
        if (ticketJpaRepository.count() > 0) {
            return;
        }

        ticketJpaRepository.save(new TicketEntity(
                "MVP-01",
                "Тестовий тікет",
                "Це перший тестовий тікет у базі SQLite.",
                LocalDate.of(2026, 4, 29)
        ));

        ticketJpaRepository.save(new TicketEntity(
                "MVP-02",
                "Тема",
                "Доброго дня, колеги! Просто тестую роботу цього сайту. Дякую",
                LocalDate.of(2026, 3, 3)
        ));

        ticketJpaRepository.save(new TicketEntity(
                "MVP-03",
                "Ноут гріється",
                "У мене Lenovo Legion 5. Десь місяць тому почав показувати температури в деяких, не у всіх іграх - 100 градусів. Допомогло вимкнення турбобуст, але це ж не вирішує проблему. Прошу замінити термопасту, і глянути до термопрокладок",
                LocalDate.of(2026, 1, 1)
        ));


        ticketJpaRepository.save(new TicketEntity(
                "MVP-04",
                "Ноут гріється",
                "Після заміни термопасти пройшло 3 дні іноут знову почав грітись.... Точно термопрокладки. Допоможіть...",
                LocalDate.of(2026, 1, 15)
        ));


        ticketJpaRepository.save(new TicketEntity(
                "MVP-05",
                "Допоможіть",
                "Монітор по різному відображає текст у Windows і у Linux. Я звик до Windows, а коли заходжу з-під Лінус то бачу більш грубий текст...",
                LocalDate.of(2026, 3, 10)
        ));


        ticketJpaRepository.save(new TicketEntity(
                "MVP-06",
                "Крадіжка",
                "Віддав ноут на заміну термопасти. Так виконавці просто зникли. Ноут вкрали. Немає. Допоможіть...",
                LocalDate.of(2026, 4, 10)
        ));


        ticketJpaRepository.save(new TicketEntity(
                "MVP-07",
                "Вкрали... допоможіть",
                "Legion 5 Lenovo. Крадіжка була здійснена вчора. Куди звертатись чи що робити?",
                LocalDate.of(2026, 4, 10)
        ));


        ticketJpaRepository.save(new TicketEntity(
                "MVP-08",
                "Новий ноут",
                "Хочу перейти з поточного Lenogo Legion 5 з RTX 2060 на щось краще і сучасніше. В мене монітор Full HD. Очікую приріт продуктивності мінімум на 50%. Ну і крім того я не можу використати потенціал мого монітора (він може видати 200 ГЦ)",
                LocalDate.of(2026, 4, 11)
        ));


        ticketJpaRepository.save(new TicketEntity(
                "MVP-09",
                "Новий ноут ai",
                "Хочу собі якийсь Lenovo Legion під мій монітор Full HD 200 ГЦ. Порадьте будь ласка. Хочу відчути аі сучасне",
                LocalDate.of(2026, 4, 11)
        ));


        ticketJpaRepository.save(new TicketEntity(
                "MVP-10",
                "Апгрейд ноута sql",
                "Чи можна зробити якісь апрейди для мого ноута? Lenovo Legion. Процесор AMD Rysen 7. Хочу приріст FPS під Full HD. Бажано без AI",
                LocalDate.of(2026, 4, 11)
        ));


        ticketJpaRepository.save(new TicketEntity(
                "MVP-11",
                "Ноут тупить crm",
                "Раніше було 200 FPS в CS2, зараз 50. Є підозра що використовується інтегрована відеокарта, а не дискретна. Lenovo Legion 7 FY9790",
                LocalDate.of(2026, 4, 11)
        ));
    }
}
