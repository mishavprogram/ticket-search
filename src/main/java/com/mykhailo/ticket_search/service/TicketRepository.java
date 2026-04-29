package com.mykhailo.ticket_search.service;

import com.mykhailo.ticket_search.model.Ticket;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TicketRepository {

    public List<Ticket> findAll() {
        return List.of(
                new Ticket("MVP-01", "Тема", "Доброго дня, колеги! Просто тестую роботу цього сайту. Дякую", LocalDate.of(2026, 3, 3)),
                new Ticket("MVP-02", "Ноут гріється", "У мене Lenovo Legion 5. Десь місяць тому почав показувати температури в деяких, не у всіх іграх - 100 градусів. Допомогло вимкнення турбобуст, але це ж не вирішує проблему. Прошу замінити термопасту, і глянути до термопрокладок", LocalDate.of(2026, 1, 1)),
                new Ticket("MVP-03", "Ноут гріється", "Після заміни термопасти пройшло 3 дні іноут знову почав грітись.... Точно термопрокладки. Допоможіть...", LocalDate.of(2026, 1, 15)),
                new Ticket("MVP-04", "Допоможіть", "Монітор по різному відображає текст у Windows і у Linux. Я звик до Windows, а коли заходжу з-під Лінус то бачу більш грубий текст...", LocalDate.of(2026, 3, 10)),
                new Ticket("MVP-05", "Крадіжка", "Віддав ноут на заміну термопасти. Так виконавці просто зникли. Ноут вкрали. Немає. Допоможіть...", LocalDate.of(2026, 4, 10)),
                new Ticket("MVP-06", "Вкрали... допоможіть", "Legion 5 Lenovo. Крадіжка була здійснена вчора. Куди звертатись чи що робити?", LocalDate.of(2026, 4, 10)),
                new Ticket("MVP-07", "Новий ноут", "Хочу перейти з поточного Lenogo Legion 5 з RTX 2060 на щось краще і сучасніше. В мене монітор Full HD. Очікую приріт продуктивності мінімум на 50%. Ну і крім того я не можу використати потенціал мого монітора (він може видати 200 ГЦ)", LocalDate.of(2026, 4, 11)),
                new Ticket("MVP-08", "Новий ноут", "Хочу собі якийсь Lenovo Legion під мій монітор Full HD 200 ГЦ. Порадьте будь ласка", LocalDate.of(2026, 4, 11)), new Ticket("MVP-09", "Апгрейд ноута", "Чи можна зробити якісь апрейди для мого ноута? Lenovo Legion. Процесор AMD Rysen 7. Хочу приріст FPS під Full HD", LocalDate.of(2026, 4, 11)),
                new Ticket("MVP-10", "Ноут тупить", "Раніше було 200 FPS в CS2, зараз 50. Є підозра що використовується інтегрована відеокарта, а не дискретна. Lenovo Legion 7 FY9790", LocalDate.of(2026, 4, 11))
        );
    }
}
