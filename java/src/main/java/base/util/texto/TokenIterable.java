package base.util.texto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import base.util.texto.StringIterable.StringIterator;

public class TokenIterable implements Iterable<TokenIterable.TokenIterator> {

	StringIterable s;
	Character separador;

	public TokenIterable(StringIterable s, Character separador) {
		this.s = s;
		this.separador = separador;
	}

	@Override
	public Iterator<TokenIterator> iterator() {
		// TODO Auto-generated method stub
		return new TokenIterator(s, separador);
	}

	public class TokenIterator implements Iterator<TokenIterator> {
		private StringIterable.StringIterator s;
		private Character separador;
		Integer ponteiro = -1;
		List<StringIterable> tokens = new ArrayList<>();

		public TokenIterator(StringIterable s, Character separador) {
			this.s = (StringIterator) s.iterator();
			this.separador = separador;
		}

		@Override
		public boolean hasNext() {
			if (s.hasNext() && tokens.size() > ponteiro + 1) {
				parseToken();
			}
			return tokens.size() > ponteiro + 1;
		}

		@Override
		public TokenIterator next() {
			if (hasNext()) {
				this.ponteiro++;
				return this;
			}
			return null;
		}

		public boolean hasPrevious() {
			return ponteiro > 0 && tokens.size() > 1;
		}

		public TokenIterator previous() {
			if (hasPrevious()) {
				this.ponteiro--;
				return this;
			}
			return null;
		}


		public void reset() {
			this.ponteiro = -1;
		}

		private void parseToken() {
			Boolean primeiroChar = Boolean.FALSE;

			String valor = "";
			while (s.hasNext()) {
				s.next();
				if (!primeiroChar && s.get() != separador) {
					primeiroChar = true;
					valor += s.get();
				} else if (primeiroChar && s.get() == separador) {
					break;
				} else {
					valor += s.get();
				}

			}
			if (!valor.isEmpty())
				tokens.add(new StringIterable(valor));
		}

		public StringIterable get() {
			return  tokens.get(ponteiro);
		}

	}

}
