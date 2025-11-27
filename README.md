> [!NOTE]
> This project is still on development.

## Creating new Action
When you create new mechanic, condition, or targeter. you need to read this guide to make consistent with other code and more optimize.

### Registration 
I make registry new system in this mod more easy, just use class SkillManager you can use code that i use in my code.

If you want add new system like 10+, i recommended to use AnnotationScanner like i use, but if you want add like 1-2. you dont really need that and will cause more power to load, just use register method if like that.

### Mechanics Guide 
In constructor is used as load the data so you can save all the data first on constructor.

In implements method like `cast`, `castAtEntity`, `castAtLocation` is how the mechanic executed, like send message or make caster jump.

When you naming the class, must add Mechanic in last name for better readability.

When you see how to make new mechanic, you know that this is exactly same as how code in Mythicmobs, but its has deference code like not use wrapper class like AbstractEntity or anything but just Entity class or World and Vec3d class.
```java
class JumpMechanic implements INoTarget {
	private final double velocity;
	
	public JumpMechanic(SkillLineConfig confg) {
		super(config);
		// You can add get method here but not execution method.
		this.velocity = config.getDouble(new String[])
	}
	
	public static void cast(SkillContext ctx) {
		Entity caster = ctx.getCaster();
		entity.setVelocity(0, 1)
	}
}
```