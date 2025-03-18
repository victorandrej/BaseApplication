package base.web;

import java.math.BigDecimal;
import java.util.Collection;

import base.web.javascript.JSIdentificadorParameter;
import base.web.javascript.JSMethodReturnTypeParameter;
import base.web.javascript.types.JSElement;

public interface SerranoJS {
	public void resolveRequests(String uuid, String response, Boolean error);

	public void finalizeObject(String uuid);

	public JSElement execFunc(String id, Object[] args);

	public Object exec(String identificador, String nome, Object[] args, JSMethodReturnTypeParameter type);

	public BigDecimal getCollectionSize(String identificador);

	public boolean isCollectionEmpty(String identificador);

	public boolean isCollectionInclude(String identificador, Object o);

	public boolean addToCollection(String identificador, JSElement e);

	public boolean removeFromCollection(String identificador, Object o);

	public boolean hasNextInCollection(String identificador, int pointeiro);

	public Object getInCollection(String identificador, int i, JSMethodReturnTypeParameter type);

	public void clearCollection(String identificador);

	public boolean addAllToCollection(String identificador, int index, Collection c);

	public <T> T setInCollection(String identificador, int index, T element);

	public <T> T removeFromCollectionIndex(String identificador, int index);

	public int indexOfCollection(String identificador, Object o);

	public int lastIndexOfCollection(String identificador, Object o);

	public  Object subListCollection(String identificador, int fromIndex, int toIndex, JSMethodReturnTypeParameter type, String identificadorLista, JSIdentificadorParameter jsIdentificadorParameter);

	public String objectToString(String identificador);
}
