package Lesson_1;

import java.util.ArrayList;

public class GenArrSwap <T> {                       //создаём класс с обобщённым типом данных
    private T[] obj;

    public GenArrSwap(T... obj) {                   //конструктор
        this.obj = obj;
    }

    @Override
    public String toString() {                              //переопределяем метод toString для удобного вывода данных
        StringBuilder stringBuilder = new StringBuilder();
        int size = obj.length;
        for (int i = 0; i < size; i++) {
            stringBuilder.append(obj[i] + " ");
        }
        return stringBuilder.toString();
    }

    public void Swap(int x, int y){                         //метод для замены местами двух элементов массива
        T temp;
        temp = obj[x];
        obj[x] = obj[y];
        obj[y] = temp;
    }

    public ArrayList arrToList(){                           //метод для записи массива в список
        int size = obj.length;
        ArrayList list = new ArrayList();
        for (int i = 0; i < size; i++) {
            list.add(i, obj[i]);
        }
        return list;
    }

}
