package Lesson_1;

abstract class Fruit {
    private String name;
    private float weight;

    public Fruit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public float getWeight() {
        return weight;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}

class Apple extends Fruit{

    public Apple(String name, float weight) {
        super(name);
        this.setWeight(weight);
    }
}

class Orange extends Fruit{

    public Orange(String name, float weight) {
        super(name);
        this.setWeight(weight);
    }
}
