package base.logs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.victorandrej.tinyioc.steriotypes.Bean;

import base.Application;


@Bean
public class LogErr extends OutputStream {


	PrintStream defaultStream = System.err;
	ThreadLocal<List<Byte>> bytes = new ThreadLocal();

  private  final  LogService logService;

  public  LogErr( LogService logService){
    this.logService = logService;
    init();
  }



	protected void init() {
		System.setErr(new PrintStream(this));
	}

  @Override
	public synchronized void write(int b) throws IOException {

		var buffer = bytes.get();

		if (Objects.isNull(buffer)) {
			buffer = new ArrayList();
			bytes.set(buffer);
		}
		buffer.add((byte) b);

		if ((char) b == '\n') {
			byte[] sbytes = new byte[buffer.size()];

			for (var i = 0; i < buffer.size(); i++)
				sbytes[i] = buffer.get(i);

			String s = new String(sbytes, Charset.forName("UTF-8"));

			if(Application.IS_DEV_MODE)
				defaultStream.print(s);

			logService.send(s, LogService.LogSeverity.ERROR);

			buffer.clear();
		}
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
	}
}
