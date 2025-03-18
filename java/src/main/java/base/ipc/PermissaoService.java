package base.ipc;

import base.ipc.exception.AccessDeniedException;

import java.util.List;

public interface PermissaoService {
  void validarPermissoes(List<String> values)  throws AccessDeniedException;
}
