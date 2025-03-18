package base.util.texto;

import java.util.Iterator;

public class StringIterable implements Iterable<StringIterable.StringIterator> {

	private String s;

	public StringIterable(String s) {
		this.s = s;
	}

	@Override
	public Iterator<StringIterator> iterator() {
		return new StringIterator(s);
	}

	public class StringIterator implements Iterator<StringIterator> {

		private String s;
		private Integer ponteiro = -1;

		public StringIterator(String s) {
			this.s = s;
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return ponteiro < s.length()-1;
		}

		@Override
		public StringIterator next() {
			this.ponteiro++;
			return this;
		}

		public boolean hasPrevious() {

			return ponteiro > 0 && s.length() > 0;
		}

		public StringIterator previous() {
			this.ponteiro--;
				return this;
		}
		public StringIterator end() {
			this.ponteiro = s.length() - 1;
			return this;
		}



		public Character get() {
			return ponteiro > -1 ? this.s.charAt(ponteiro):null;
		}

		public Character getPrevious() {
			return this.hasPrevious() ? this.s.charAt(ponteiro - 1) : null;
		}

		public Character getNext() {
			return this.hasNext() ? this.s.charAt(ponteiro + 1) : null;
		}

		public void reset() {
			this.ponteiro = -1;
		}



		@Override
		public String toString() {

			return s;
		}
	}

}
