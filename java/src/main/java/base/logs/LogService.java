package base.logs;

import java.io.IOException;


import io.github.victorandrej.tinyioc.steriotypes.Bean;


import base.ipc.Allowed;


@Bean
@Allowed
public class LogService {

  private FileLogService fileLogService ;

  public LogService(FileLogService fileLogService){
    this.fileLogService = fileLogService;
  }



  public Log send(String line, LogSeverity severity) throws IOException {

    fileLogService.write(line,severity);

    var log = new Log();

    log.line = line;
    log.severity = severity;

    return log;

  }

  public FileLogService.FileLog getLogs(Long ponteiro,Integer rows) throws IOException {
    return  this.fileLogService.read(ponteiro,rows);
  }


  public static class Log {
    String line;
    LogSeverity severity;
  }

  public enum LogSeverity {

    ERROR((byte) 0x01), INFO((byte) 0x02), DEFAUT((byte) 0x03), WARNING((byte) 0x04), UNKNOW((byte) 0x05);
    byte value;

    LogSeverity(byte value) {
      this.value = value;
    }

    public  static  LogSeverity parse(byte severity) {
      switch (severity) {
        case 0x01:
          return ERROR;
        case 0x02:
          return INFO;
        case 0x03:
          return DEFAUT;
        case 0x04:
          return WARNING;
        default:
          return UNKNOW;
      }
    }
  }
}
