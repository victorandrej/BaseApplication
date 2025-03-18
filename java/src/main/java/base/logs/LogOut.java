package base.logs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import base.Application;
import io.github.victorandrej.tinyioc.steriotypes.Bean;


@Bean
public class LogOut extends OutputStream {

	PrintStream defaultStream = System.out;


 private  final 	LogService logService;

  public  LogOut(LogService logService){
    this.logService = logService;
    init();
  }

	ThreadLocal<List<Byte>> bytes = new ThreadLocal();


	protected void init() {

    System.setOut(new PrintStream(this));
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

			if (Application.IS_DEV_MODE)
				defaultStream.print(s);

			logService.send(s, LogService.LogSeverity.DEFAUT);
			buffer.clear();
		}
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
	}
}
