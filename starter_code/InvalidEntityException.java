/**
 * Exception thrown when trying to create an invalid entity type.
 */

	public class InvalidEntityException extends RuntimeException  { 

			public InvalidEntityException(String className) { 
				super("Invalid entity" + className);
			}
				
	}

	

