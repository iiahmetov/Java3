package Lesson_1;


public class MainClass1 {
    public static void main(String[] args) {
//        GenArrSwap <String> arrStr = new GenArrSwap<>("1", "2", "4", "3");  //инициализируем массив строк
//        System.out.println(arrStr);                                               //вывод массива (упрощён, т.к. переопределён метод toString)
//        arrStr.Swap(2,3);                                                   //меняем местами 2 и 3 элемент
//        System.out.println(arrStr);
//
//        GenArrSwap <Integer> arrInt = new GenArrSwap<>(12, 11, 13, 14);     //инициализируем массив чисел
//        System.out.println(arrInt);
//        arrInt.Swap(1,0);                                                   //меняем местами 0 и 1 элемент
//        System.out.println(arrInt);
//
//        ArrayList arrList = arrStr.arrToList();                                   //создаём список из массива строк
//        System.out.println(arrList);
//        System.out.println(arrList.get(2));

        Apple apple = new Apple("АНТОНОВКААА", 1.0f);       //создали объект класса Яблоко
        Orange orange = new Orange("Марокко", 1.5f);        //создали объект класса Апельсин

        Box orangeBox = new Box();                                       //создали пустую коробку
        Box<Fruit> appleBox = new Box<>(apple, 1);                  //создали коробку с яблоком
        Box<Fruit> appleBox2 = new Box<>(apple, 3);                 //создали коробку с яблоками №2

        System.out.println(orangeBox.getWeight());
        System.out.println(appleBox.getWeight());
        System.out.println(orangeBox.compareTo(appleBox));    //сравнили вес коробки с яблоком и коробки с апельсином

        orangeBox.addFruit(orange, 4);              //добавили в пустую коробку 4 апельсина
//        orangeBox.addFruit(apple, 1);        //при попытке добавить в коробку с апельсинами яблоко выдаст эксепшн
        appleBox.addFruit(apple, 2);            //добавили в коробку с яблоком ещё 2 яблока
//        appleBox.sprinkleFrom(appleBox); //выдаст эксепшн при попытке пересыпать из коробки в которую хотим пересыпать

        appleBox.sprinkleFrom(appleBox2);       //пересыпаем из коробки с яблоками №2 в коробку с яблоками
        System.out.println(appleBox2.getWeight());  //проверяем, что коробка с яблоками №2 опустела ииии
        appleBox2.addFruit(orange, 2);       //и без проблем закидываем в неё апельсины

        System.out.println(appleBox.getWeight());
        System.out.println(orangeBox.getWeight());
        System.out.println(orangeBox.compareTo(appleBox));  //сравнили вес коробки с 6 яблоками и коробки с 4 апельсинами



    }
}
