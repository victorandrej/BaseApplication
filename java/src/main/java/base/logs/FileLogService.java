package base.logs;

import io.github.victorandrej.tinyioc.steriotypes.Bean;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Bean
public class FileLogService {

  private static final String UTF_8 = "UTF-8";
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final Path LOGSPATH = Paths.get("./logs/");
  private static final String LOG_FILE_NAME = "serrano.log";

  private static final char NEW_LINE = '\n';


  private String getFileName() {
    return "log." + DATE_FORMAT.format(LocalDate.now()) + ".log";
  }

  private File resolveLogFile() throws IOException {
    File logPath = LOGSPATH.toFile();
    logPath.mkdirs();
    var f = LOGSPATH.resolve(getFileName()).toFile();
    ;
    if (!f.exists())
      f.createNewFile();
    return f;
  }

  public void write(String log, LogService.LogSeverity severity) throws IOException {

    File logFile = resolveLogFile();

    log = log.replace("\n", "").replace("\r", "");

    try (FileWriter fw = new FileWriter(logFile,true)) {
      fw.write((char) severity.value);
      fw.write(log);
      fw.write(NEW_LINE);
      fw.flush();
    }

  }

  public FileLog read(long startPosition, int rows) throws IOException {

    File logFile = resolveLogFile();

    try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
      long fileLength = raf.length();

      long pointer = (startPosition < fileLength && startPosition > -1) ? startPosition : fileLength - 1;

      List<Byte> line = new ArrayList<>();
      List<LogService.Log> logs = new ArrayList<>();
      while (pointer >= 0) {
        raf.seek(pointer);
        var b = raf.readByte();

        if (!line.isEmpty() && (b == NEW_LINE || pointer == 0)) {


          Collections.reverse(line);
          var severity = LogService.LogSeverity.parse(line.remove(0));

          var arr = new byte[line.size()];

          for (var i = 0; i < arr.length; i++)
            arr[i] = line.get(i);



          var log = new LogService.Log();
          log.line = new String(arr, UTF_8);
          log.severity = severity;
          logs.add(log);
          line.clear();

        } else if(b != NEW_LINE) {
          line.add(b);
        }
        pointer--;

        if (logs.size() == rows || pointer < 0)
          break;

      }
      Collections.reverse(logs);
      return new FileLog(pointer, logs);
    }
  }

  public class FileLog {
    List<LogService.Log> logs;
    long currentPosition;


    public FileLog(long currentPosition, List<LogService.Log> logs) {
      this.currentPosition = currentPosition;
      this.logs = logs;
    }

    public List<LogService.Log> getLogs() {
      return logs;
    }

    public long getCurrentPosition() {
      return currentPosition;
    }
  }

}
