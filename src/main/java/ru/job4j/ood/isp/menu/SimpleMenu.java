package ru.job4j.ood.isp.menu;

import java.util.*;

public class SimpleMenu implements Menu {

    private final List<MenuItem> rootElements = new ArrayList<>();

    @Override
    public boolean add(String parentName, String childName, ActionDelegate actionDelegate) {
        if (parentName == ROOT) {
            return rootElements.add(new SimpleMenuItem(childName, actionDelegate));
        }
        Optional<ItemInfo> parent = findItem(parentName);
        if (parent.isEmpty()) {
            return false;
        }
        return parent.get().menuItem.getChildren().add(new SimpleMenuItem(childName, actionDelegate));
    }

    @Override
    public Optional<MenuItemInfo> select(String itemName) {
        return findItem(itemName).map(item -> new MenuItemInfo(item.menuItem, item.number));
    }

    @Override
    public Iterator<MenuItemInfo> iterator() {
        return new Iterator<>() {
            private final DFSIterator iterator = new DFSIterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public MenuItemInfo next() {
                ItemInfo item = iterator.next();
                return new MenuItemInfo(item.menuItem, item.number);
            }
        };
    }

    private Optional<ItemInfo> findItem(String name) {
        Iterator<ItemInfo> iterator = new DFSIterator();
        while (iterator.hasNext()) {
            ItemInfo item = iterator.next();
            if (item.menuItem.getName().equals(name)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    private static class SimpleMenuItem implements MenuItem {

        private String name;
        private List<MenuItem> children = new ArrayList<>();
        private ActionDelegate actionDelegate;

        public SimpleMenuItem(String name, ActionDelegate actionDelegate) {
            this.name = name;
            this.actionDelegate = actionDelegate;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<MenuItem> getChildren() {
            return children;
        }

        @Override
        public ActionDelegate getActionDelegate() {
            return actionDelegate;
        }
    }

    private class DFSIterator implements Iterator<ItemInfo> {

        private final Deque<MenuItem> stack = new LinkedList<>();

        private final Deque<String> numbers = new LinkedList<>();

        DFSIterator() {
            int number = 1;
            for (MenuItem item : rootElements) {
                stack.addLast(item);
                numbers.addLast(String.valueOf(number++).concat("."));
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public ItemInfo next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            MenuItem current = stack.removeFirst();
            String lastNumber = numbers.removeFirst();
            List<MenuItem> children = current.getChildren();
            int currentNumber = children.size();
            for (var i = children.listIterator(children.size()); i.hasPrevious();) {
                stack.addFirst(i.previous());
                numbers.addFirst(lastNumber.concat(String.valueOf(currentNumber--)).concat("."));
            }
            return new ItemInfo(current, lastNumber);
        }
    }

    private class ItemInfo {

        private final MenuItem menuItem;
        private final String number;

        public ItemInfo(MenuItem menuItem, String number) {
            this.menuItem = menuItem;
            this.number = number;
        }
    }
}
