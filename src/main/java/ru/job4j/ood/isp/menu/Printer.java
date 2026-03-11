package ru.job4j.ood.isp.menu;

public class Printer implements MenuPrinter {
    @Override
    public void print(Menu menu) {
        menu.forEach(item -> {
            int level = (int) item.getNumber().chars().filter(ch -> ch == '.').count() - 1;
            System.out.println("----".repeat(level) + item.getName());
        });
    }
}
