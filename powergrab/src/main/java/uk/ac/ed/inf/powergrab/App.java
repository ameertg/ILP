package uk.ac.ed.inf.powergrab;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Position pos = new Position(55.946233, -3.192473);
        System.out.println(pos.nextPosition(Direction.N).latitude);
        System.out.println(pos.nextPosition(Direction.N).inPlayArea());
    }
}
