package Animal;

class Animal {
	public static void main(String[] args) {
		
	}
}

abstract class Mammal extends Animal {
 public abstract String bark();
 public abstract String getFood();
}

abstract class Reptile extends Animal {
 public abstract String getFood();
}

final class Cat extends Mammal {
 private String name;
 private float weight;
 private String nameSlave;

 @Override
 public String bark() {
     return "Meow";
 }

 @Override
 public String getFood() {
     return "Fish";
 }

 public Cat(String name, float weight, String nameSlave) {
	super();
	this.name = name;
	this.weight = weight;
	this.nameSlave = nameSlave;
}

public String getName() { return name; }
 public void setName(String name) { this.name = name; }

 public float getWeight() { return weight; }
 public void setWeight(float weight) { this.weight = weight; }

 public String getNameSlave() { return nameSlave; }
 public void setNameSlave(String nameSlave) { this.nameSlave = nameSlave; }
}

final class Dog extends Mammal {
 private String name;
 private float weight;
 private String nameMaster;

 @Override
 public String bark() {
     return "Bowbow";
 }

 @Override
 public String getFood() {
     return "Apple";
 }

 public Dog(String name, float weight, String nameMaster) {
	super();
	this.name = name;
	this.weight = weight;
	this.nameMaster = nameMaster;
}

public String getName() { return name; }
 public void setName(String name) { this.name = name; }

 public float getWeight() { return weight; }
 public void setWeight(float weight) { this.weight = weight; }

 public String getNameMaster() { return nameMaster; }
 public void setNameMaster(String nameMaster) { this.nameMaster = nameMaster; }
}

final class Crocodile extends Reptile {
 private String name;
 private float weight;

 @Override
 public String getFood() {
     return "Meat";
 }

 public Crocodile(String name, float weight) {
	super();
	this.name = name;
	this.weight = weight;
}

public String getName() { return name; }
 public void setName(String name) { this.name = name; }

 public float getWeight() { return weight; }
 public void setWeight(float weight) { this.weight = weight; }
}