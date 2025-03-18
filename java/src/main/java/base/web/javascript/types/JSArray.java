package base.web.javascript.types;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import base.web.javascript.JSIdentificadorParameter;
import base.web.javascript.JSInstanceTree;
import base.web.javascript.JSMethodReturnTypeParameter;
import base.web.javascript.JSRetorno;
import base.web.javascript.JSTypeResolver;
import base.web.javascript.JavaScript.Execution;

public class JSArray<T> extends JSElement implements List<T> {
	private String identificador;
	private Class<?> type;

	public JSArray(Execution exec, JSRetorno retorno) {
		super(JSElementType.ARRAY, exec, retorno);

		this.identificador = exec.identificador;
		JSInstanceTree.put(null, this.identificador, this.exec.serranoJs);
		this.type = JSTypeResolver.getType(this.exec.returnType);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		return method.invoke(this, args);
	}

	@Override
	public JSArray get() {
		return this;
	}

	public String getIdentificador() {
		return this.identificador;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!this.ignoreFinalize)
			JSInstanceTree.delete(identificador);
	}

	@Override
	public Iterator<T> iterator() {
		return new JSArrayIterator(this, this.type);
	}

	@Override
	public int size() {
		return this.getExec().serranoJs.getCollectionSize(this.identificador).intValue();

	}

	@Override
	public boolean isEmpty() {
		return this.getExec().serranoJs.isCollectionEmpty(this.identificador);
	}

	@Override
	public boolean contains(Object o) {

		return this.getExec().serranoJs.isCollectionInclude(this.identificador, o);
	}

	@Override
	public Object[] toArray() {
		var size = this.size();
		Object[] arr = new Object[size];
		for (int i = 0; i < size; i++)
			arr[i] = this.get(i);

		return arr;

	}

	@Override
	public <T> T[] toArray(T[] a) {
		var size = this.size();
		T[] arr = (T[]) Array.newInstance(a.getClass(), size);
		for (int i = 0; i < size; i++)
			arr[i] = (T) this.get(i);

		return arr;
	}

	@Override
	public boolean add(T e) {
		if (JSElement.class.isAssignableFrom(e.getClass()))
			throw new IllegalArgumentException("Apenas Elementos Js sao permitidos");

		return this.getExec().serranoJs.addToCollection(this.identificador, (JSElement) e);
	}

	@Override
	public boolean remove(Object o) {
		if (JSElement.class.isAssignableFrom(o.getClass()))
			throw new IllegalArgumentException("Apenas Elementos Js sao permitidos");

		return this.getExec().serranoJs.removeFromCollection(this.identificador, o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (var el : c)
			if (JSElement.class.isAssignableFrom(el.getClass()))
				throw new IllegalArgumentException("Apenas Elementos Js sao permitidos");
			else if (!this.contains(el))
				return false;
		return true;

	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean mudanca = false;
		for (var el : c)
			mudanca = this.add(el) || mudanca;
		return mudanca;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean mudanca = false;
		for (var el : c)
			mudanca = this.remove(el) || mudanca;
		return mudanca;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		this.clear();

		this.addAll((Collection<? extends T>) c);
		throw new RuntimeException("Nao implementado");
	}

	@Override
	public T get(int index) {
		return (T) exec.serranoJs.getInCollection(identificador, index, new JSMethodReturnTypeParameter(this.type));
	}

	@Override
	public void clear() {
		this.exec.serranoJs.clearCollection(identificador);

	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return this.exec.serranoJs.addAllToCollection(this.exec.identificador, index, c);
	}

	@Override
	public T set(int index, T element) {

		return this.exec.serranoJs.setInCollection(this.exec.identificador, index, element);
	}

	@Override
	public void add(int index, T element) {
		addAll(index, Collections.singleton(element));

	}

	@Override
	public T remove(int index) {

		return this.exec.serranoJs.removeFromCollectionIndex(this.exec.identificador, index);
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return this.exec.serranoJs.indexOfCollection(this.exec.identificador, o);
	}

	@Override
	public int lastIndexOf(Object o) {

		return this.exec.serranoJs.lastIndexOfCollection(this.exec.identificador, o);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		String identificadorLista = "__SUBLISTA" + UUID.randomUUID().toString() + "SUBLISTA__";
		return (List<T>) this.exec.serranoJs.subListCollection(this.exec.identificador, fromIndex, toIndex,
				new JSMethodReturnTypeParameter(this.type), identificadorLista,
				new JSIdentificadorParameter(identificadorLista));
	}

	@Override
	public ListIterator<T> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	class JSArrayIterator<T> implements Iterator<T> {
		int pointeiro = 0;
		int length;
		JSArray jsArray;
		Class<?> returnType;

		public JSArrayIterator(JSArray jsArray, Class<?> returnType) {
			this.jsArray = jsArray;
			this.returnType = returnType;
		}

		@Override
		public boolean hasNext() {

			return jsArray.getExec().serranoJs.hasNextInCollection(jsArray.identificador, pointeiro + 1);
		}

		@Override
		public T next() {
			// TODO Auto-generated method stub
			return (T) jsArray.getExec().serranoJs.getInCollection(jsArray.identificador, pointeiro++,
					new JSMethodReturnTypeParameter(returnType));
		}

	}

	public class JSArrayProxy implements InvocationHandler {

		JSArray jsArray;

		public JSArrayProxy(JSArray jsArray) {
			this.jsArray = jsArray;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// TODO Auto-generated method stub
			return jsArray.invoke(proxy, method, args);
		}

	}

	@Override
	public String toString() {
		var s = "[ ";

		for(var o : this)
			s += o.toString() +", ";
		s = s.substring(s.length()-2);

		return s + "]";
	}

}
