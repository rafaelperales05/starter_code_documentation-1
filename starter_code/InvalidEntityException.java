/**
 * Exception thrown when trying to create an invalid entity type.
 * 
 * TODO: Make sure checked/unchecked is correct
 *       Think about whether this is a checked or unchecked exception.
 */

	public class InvalidEntityException extends Exception  { 

			public InvalidEntityException(String className) { 
				super("Invalid entity" + className);
			}
				
	}

	

