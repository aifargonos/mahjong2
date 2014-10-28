package mahjong.engine;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Coordinates implements Comparable<Coordinates> {
	
	
	
	public static final Coordinates NW = new Coordinates( 0, -1,  0);
	public static final Coordinates NE = new Coordinates( 1, -1,  0);
	public static final Coordinates SW = new Coordinates( 0,  2,  0);
	public static final Coordinates SE = new Coordinates( 1,  2,  0);
	public static final Coordinates WN = new Coordinates(-1,  0,  0);
	public static final Coordinates WS = new Coordinates(-1,  1,  0);
	public static final Coordinates EN = new Coordinates( 2,  0,  0);
	public static final Coordinates ES = new Coordinates( 2,  1,  0);
	public static final Coordinates ANW = new Coordinates( 0,  0,  1);
	public static final Coordinates ANE = new Coordinates( 1,  0,  1);
	public static final Coordinates ASW = new Coordinates( 0,  1,  1);
	public static final Coordinates ASE = new Coordinates( 1,  1,  1);
	public static final Coordinates UNW = new Coordinates( 0,  0, -1);
	public static final Coordinates UNE = new Coordinates( 1,  0, -1);
	public static final Coordinates USW = new Coordinates( 0,  1, -1);
	public static final Coordinates USE = new Coordinates( 1,  1, -1);
	
	
	
	public int x;
	public int y;
	public int z;
	
	public Coordinates() {
		reset();
	}
	public Coordinates(int x, int y, int z) {
		set(x, y, z);
	}
	public Coordinates(Coordinates c) {
		set(c);
	}
	
	public void reset() {
		x = y = z = 0;
	}
	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void set(Coordinates c) {
		this.x = c.x;
		this.y = c.y;
		this.z = c.z;
	}
	
	public int compareTo(Coordinates c) {
		if(z < c.z) return -1;
		if(z > c.z) return 1;
		if(x + y < c.x + c.y) return -1;
		if(x + y > c.x + c.y) return 1;
		if(x < c.x) return -1;
		if(x > c.x) return 1;
		return 0;
	}
	
	public Coordinates getTranslatedCopy(int dx, int dy, int dz) {
		return new Coordinates(x + dx, y + dy, z + dz);
	}
	
	public Coordinates getTranslatedCopy(Coordinates d) {
		return getTranslatedCopy(d.x, d.y, d.z);
	}
	
	public Coordinates translate(int dx, int dy, int dz) {
		x += dx;
		y += dy;
		z += dz;
		return this;
	}
	
	public Coordinates translate(Coordinates d) {
		return translate(d.x, d.y, d.z);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Coordinates)) return false;
		Coordinates c = (Coordinates)o;
		return x == c.x && y == c.y && z == c.z;
	}
	
	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}
	
}
