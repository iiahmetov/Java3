package Lesson_1;

import java.util.ArrayList;

public class Box<T extends Fruit> {
    private ArrayList<T> fruit = new ArrayList<>();
    private float weight = 0;                   //базовый вес коробки

    public Box() {                              //конструктор для пустой коробки
    }

    public Box(T fruit, int num) {              //конструктор для коробки с содержимым
        if (num < 1) {
            throw new IllegalArgumentException("Число добавляемых фруктов не может быть меньше 1.");
        }
        for (int i = 0; i < num; i++) {
            this.fruit.add(fruit);
        }
    }

    public float getWeight(){               //метод получения веса коробки
        float m = weight;
        if (!fruit.isEmpty()){
            for (T f: fruit) {
                m += f.getWeight();
            }
        }
        return m;
    }

    public boolean compareTo(Box<T> box){       //метод сравнения веса двух коробок
        if (this.getWeight() == box.getWeight()){
            return true;
        }
        return false;
    }

    public void addFruit(T fruit, int num){         //метод добавления фрукта/фруктов в коробку
        if (num < 1) {
            throw new IllegalArgumentException("Число добавляемых фруктов не может быть меньше 1.");
        }
        if (!this.fruit.isEmpty() && this.fruit.get(0).getClass() != fruit.getClass()){
            throw new IllegalArgumentException("Нельзя класть " + fruit.getName() + " в коробку с " + this.fruit.get(0).getName() + "!");
        }
        for (int i = 0; i < num; i++) {
            this.fruit.add(fruit);
        }
    }

    public void sprinkleFrom(Box<T> box){   //метод для того, чтобы пересыпать в коробку содержимое другой коробки
        if (this == box){
            throw new IllegalArgumentException("Нельзя пересыпать в себя!");
        }
        if (!this.fruit.isEmpty() && this.fruit.get(0).getClass() != box.fruit.get(0).getClass()){
            throw new IllegalArgumentException("Нельзя класть " + box.fruit.get(0).getName() + " в коробку с " + this.fruit.get(0).getName() + "!");
        }
        for (int i = 0; i < box.fruit.size(); i++) {
            this.fruit.add(box.fruit.get(i));
        }
        box.fruit.clear();
    }

}
