package fr.ul.theascendofpluton.Exceptions;

/**
 * Cette exception est levée lorsque le niveau est mal formé.
 */
public class LevelLoadException extends Exception{
    public LevelLoadException(String msg){
        super(msg);
    }

}
