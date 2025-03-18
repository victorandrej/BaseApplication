INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.usuario','Exibir Cadastro de Usuario');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.usuario.editar','Editar Usuario');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.usuario.excluir','excluir Usuario');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.usuario.incluir','Cadastrar Usuario');;

INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.pessoa','Exibir Cadastro de Pessoa');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.pessoa.editar','Editar Pessoa');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.pessoa.excluir','excluir Pessoa');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.pessoa.incluir','Cadastrar Pessoa');;

CREATE TABLE IF NOT EXISTS PESSOA(
  ID BIGSERIAL PRIMARY KEY NOT NULL,
  NOME VARCHAR NOT NULL,
  DESCRICAO VARCHAR,
  ATIVO BOOLEAN,
  CPF_CNPJ VARCHAR NOT NULL,
  TELEFONE VARCHAR,
  PICTURE BYTEA,
  DATA_NASCIMENTO DATE,
  RG VARCHAR,
  GENERO VARCHAR,
  INSCRICAO_ESTADUAL VARCHAR,
  INSCRICAO_MUNICIPAL VARCHAR,
  IS_EMPRESA BOOLEAN
);;

GRANT ALL PRIVILEGES ON TABLE PESSOA TO SERRANO;;
 
CREATE TABLE IF NOT EXISTS ENDERECO_PESSOA(
	  ID BIGSERIAL PRIMARY KEY NOT NULL,
	  ENDERECO VARCHAR,
	  COMPLEMENTO VARCHAR,
	  NUMERO VARCHAR,
	  CIDADE VARCHAR,
	  UF VARCHAR,
	  ID_PESSOA BIGINT,
	  IS_PRINCIPAL BOOLEAN,
	  BAIRRO VARCHAR,
	  CODIGO_MUNICIPIO VARCHAR,
	  CEP VARCHAR,
	  CODIGO_PAIS varchar
);;


GRANT ALL PRIVILEGES ON TABLE ENDERECO_PESSOA TO SERRANO;;

CREATE TABLE IF NOT EXISTS USUARIO_PREFERENCIA(
	ID BIGSERIAL PRIMARY KEY NOT NULL,
	NOME VARCHAR NOT NULL,
	VALOR TEXT NOT NULL,
	ID_USUARIO BIGINT
);;

GRANT ALL PRIVILEGES ON TABLE USUARIO_PREFERENCIA TO SERRANO;;

CREATE TABLE IF NOT EXISTS  RELATORIO(
  ID BIGSERIAL NOT NULL PRIMARY KEY,
  NOME VARCHAR NOT NULL,
  DESCRICAO VARCHAR NOT NULL,
  IS_RESOURCE BOOLEAN,
  ARQUIVO BYTEA,
  VISIBILIDADE VARCHAR,
  FILE_NOME VARCHAR
);;

GRANT ALL PRIVILEGES ON TABLE RELATORIO TO SERRANO;;

CREATE TABLE IF NOT EXISTS  RELATORIO_PARAMETRO(
  ID BIGSERIAL NOT NULL PRIMARY KEY,
  NOME VARCHAR NOT NULL,
  DESCRICAO VARCHAR NOT NULL,
  VALOR VARCHAR,
  DATA BYTEA,
  IS_FILE BOOLEAN,
  VISIBILIDADE VARCHAR,
  ID_RELATORIO BIGINT NOT NULL,
  FILE_NOME VARCHAR
);;

GRANT ALL PRIVILEGES ON TABLE RELATORIO_PARAMETRO TO SERRANO;;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;




INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('planejamento','Exibir Planejamento');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('planejamento.relatorio','Exibir Relatorios');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('planejamento.relatorio.editar','Editar Relatorios');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('planejamento.relatorio.excluir','excluir Relatorios');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('planejamento.relatorio.incluir','Cadastrar Relatorios');;





CREATE TABLE IF NOT EXISTS  PRODUTO(
  ID BIGSERIAL NOT NULL PRIMARY KEY,
  NOME VARCHAR NOT NULL,
  NCM INTEGER,
  EAN INTEGER,
  CFOP INTEGER,
  CEST INTEGER,
  ID_UNIDADE_MEDIDA BIGINT NOT NULL,
  escala_relevante boolean,
  id_pessoa_fabricante bigint,
  origem varchar,
  tributacao varchar,
  csosn varchar,
  id_cfop bigint
  
);;

GRANT ALL PRIVILEGES ON TABLE PRODUTO TO SERRANO;;


CREATE TABLE IF NOT EXISTS  CFOP(
	codigo integer primary key not null,
	descricao varchar
);;

GRANT ALL PRIVILEGES ON TABLE CFOP TO SERRANO;;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;



INSERT INTO CFOP(CODIGO,DESCRICAO)
values('5101','Venda de produção do estabelecimento');;



CREATE TABLE IF NOT EXISTS  UNIDADE_MEDIDA(
  ID BIGSERIAL NOT NULL PRIMARY KEY,
  NOME VARCHAR NOT NULL,
  EXPRESSAO VARCHAR NOT NULL,
  ID_PAI BIGINT
);;


GRANT ALL PRIVILEGES ON TABLE UNIDADE_MEDIDA TO SERRANO;;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;



INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.produto','Exibir Cadastro de Produto');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.produto.editar','Editar Produto');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.produto.excluir','Excluir Produto');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.produto.incluir','Cadastrar Produto');;


CREATE TABLE  IF NOT EXISTS  MOVIMENTACAO(
ID BIGSERIAL NOT NULL PRIMARY KEY,
ID_RATEIRO BIGINT,
ID_COMPOSICAO BIGINT,
TIPO VARCHAR NOT NULL,
IS_BAIXADO BOOLEAN,
DATA DATE NOT NULL,
DATA_ALTERACAO DATE NOT NULL,
DATA_BAIXA DATE,
VALOR_DESCONTO REAL NOT NULL,
VALOR_BRUTO REAL NOT NULL,
VALOR_LIQUIDO REAL NOT NULL
);;

GRANT ALL PRIVILEGES ON TABLE MOVIMENTACAO TO SERRANO;;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;



INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro','Exibir Financeiro');;

INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro.movimentacao','Exibir Movimentacao Financeira');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro.movimentacao.editar','Editar Movimentacao');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro.movimentacao.excluir','Excluir Movimentacao');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro.movimentacao.incluir','Cadastrar Movimentacao');;

INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial','Exibir Comercial');;

INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.estoque','Exibir Estoque');;

CREATE TABLE IF NOT EXISTS ESTOQUE(
	ID BIGSERIAL PRIMARY KEY NOT NULL,
	NOME VARCHAR NOT NULL
	
);;


GRANT ALL PRIVILEGES ON TABLE ESTOQUE TO SERRANO;;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;



CREATE TABLE  IF NOT EXISTS  ESTOQUE_PRODUTO(
	ID BIGSERIAL PRIMARY KEY NOT NULL,
	ID_ESTOQUE BIGINT NOT NULL,
	ID_PRODUTO BIGINT NOT NULL,
	QUANTIDADE REAL
);;

GRANT ALL PRIVILEGES ON TABLE ESTOQUE_PRODUTO TO SERRANO;;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;



INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.estoque.editar','Editar Movimentacao');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.estoque.excluir','Excluir Movimentacao');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.estoque.incluir','Cadastrar Movimentacao');;

INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.estoque.produto','Exibir Produtos do estoque');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.estoque.produto.editar','Editar Movimentacao');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.estoque.produto.excluir','Excluir Movimentacao');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.estoque.produto.incluir','Cadastrar Movimentacao');;

INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro.preco','Exibir Preços');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro.preco.editar','Editar Preco');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro.preco.excluir','Excluir Preco');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('financeiro.preco.incluir','Cadastrar Preco');;


CREATE TABLE  IF NOT EXISTS  PRECO(
	ID BIGSERIAL PRIMARY KEY NOT NULL,
	VALOR REAL,
	ID_UNIDADE_MEDIDA BIGINT NOT NULL,
	ID_PRODUTO BIGINT NOT NULL
	
);; 


GRANT ALL PRIVILEGES ON TABLE PRECO TO SERRANO;;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;




INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.checkout','Exibir Checkout');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.checkout.incluir','Inclur Venda no checkout');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.checkout.editar','Editar Venda no checkout');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.checkout.excluir','Excluir Venda no checkout');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('comercial.checkout.confirmar1','Confirmar  Venda no checkout');;

INSERT INTO public.unidade_medida(
	 nome, expressao)
	VALUES ('KG','x');;


INSERT INTO public.produto( nome, ncm, cest, id_unidade_medida)
	VALUES ( 'Buffet', '21069090', '1709700', (SELECT last_value FROM unidade_medida_id_seq));;
	
INSERT INTO PARAMETROS(NOME,DESCRICAO,VALUE,TIPO) VALUES('buffet','Buffet','','PASTA');;
INSERT INTO PARAMETROS(NOME,DESCRICAO,VALUE,TIPO) VALUES('buffet.id','ID do produto buffet',(SELECT last_value FROM produto_id_seq),'READONLY');;


CREATE   TABLE  IF NOT EXISTS  PEDIDO_VENDA(
	ID BIGSERIAL NOT NULL PRIMARY KEY,
	PORCENTAGEM_DESCONTO REAL NOT NULL,
	VALOR_TOTAL REAL NOT NULL,
	mesa varchar
);;

GRANT ALL PRIVILEGES ON TABLE PEDIDO_VENDA TO SERRANO;;

CREATE TABLE  IF NOT EXISTS  PEDIDO_PRODUTO(
	ID BIGSERIAL NOT NULL PRIMARY KEY,
	QUANTIDADE REAL NOT NULL,
	VALOR REAL NOT NULL,
	PORCENTAGEM_DESCONTO REAL NOT NULL,
	VALOR_TOTAL REAL NOT NULL,
	ID_PRODUTO BIGINT NOT NULL,
	ID_UNIDADE_MEDIDA BIGINT NOT NULL,
	ID_PEDIDO BIGINT NOT NULL,
	COMPOE_NOTA BOOLEAN,
	ID_CFOP BIGINT
);;
GRANT ALL PRIVILEGES ON TABLE PEDIDO_PRODUTO TO SERRANO;;
	

    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;



INSERT  INTO  RELATORIO(nome,descricao,is_resource ,visibilidade)
VALUES('checkout/Comanda.jrxml','Comanda',true,'EDIT_ONLY');;

INSERT  INTO RELATORIO_PARAMETRO(NOME,DESCRICAO,ID_RELATORIO,VISIBILIDADE,is_file)
VALUES('LOGO','Logo',(SELECT last_value FROM relatorio_id_seq),'EDIT_ONLY',true);;

INSERT  INTO RELATORIO_PARAMETRO(NOME,DESCRICAO,ID_RELATORIO,VISIBILIDADE,is_file)
VALUES('CODE_BAR','Codigo de barras',(SELECT last_value FROM relatorio_id_seq),'READ_ONLY',true);;


INSERT INTO PARAMETROS(NOME,DESCRICAO,VALUE,TIPO) VALUES('fiscal','Fiscal','','PASTA');;
INSERT INTO PARAMETROS(NOME,DESCRICAO,VALUE,TIPO) VALUES('fiscal.config','Configurações','','PASTA');;
 
CREATE TABLE empresa (
    id BIGSERIAL PRIMARY KEY NOT NULL,
  	id_pessoa BIGINT NOT NULL,
    expressao_icms VARCHAR,
    expressao_pis VARCHAR,
    expressao_cofins VARCHAR,
    expressao_csll VARCHAR,
    numero_cupom_fiscal integer ,
    serie_cupom_fiscal integer,
    regime_tributario varchar,
    aliquota_icms varchar,
    modalidade_base_calculo_icms varchar,
    token_csc varchar,
    id_token_csc varchar,
    is_simples_nacional boolean
);;

GRANT ALL PRIVILEGES ON TABLE empresa TO SERRANO;;

CREATE TABLE PAIS (
	codigo varchar primary key not null,
	nome varchar not null
);;

GRANT ALL PRIVILEGES ON TABLE PAIS TO SERRANO;;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;


INSERT INTO PAIS(CODIGO,NOME)
VALUES('1058','Brasil');;



INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.empresa','Exibir Empresa');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.empresa.incluir','Inclur Empresa');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.empresa.editar','Editar Empresa');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.empresa.excluir','Excluir Empresa');;



INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.cfop','Exibir CFOP');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.cfop.incluir','Inclur CFOP');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.cfop.editar','Editar CFOP');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.cfop.excluir','Excluir CFOP');;


INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.pais','Exibir Pais');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.pais.incluir','Inclur Pais');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.pais.editar','Editar Pais');;
INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('cadastro.pais.excluir','Excluir Pais');;

CREATE TABLE venda (
    id bigserial PRIMARY KEY,
    protocolo VARCHAR,
    chave_nfe VARCHAR,
    valor_total_icms real,
    valor_total_pis real,
    valor_total_icms_st real,
    valor_total_base_calculo_icms_st real,
    valor_total_cofins real,
    estado varchar,
    valor_total_nota real,
    valor_total_produtos real,
    valor_total_base_calculo_icms real,
    valor_total_base_calculo_pis real,
    valor_total_base_calculo_cofins real,
    tipo_venda VARCHAR,
    tipo_cliente VARCHAR,
    destino_operacao VARCHAR,
    versao_aplicativo VARCHAR,
    processo_de_emissao VARCHAR,
    finalidade_nota VARCHAR,
    ambiente VARCHAR,
    digito_verificador VARCHAR,
    forma_de_emissao VARCHAR,
    formato_de_impressao VARCHAR,
    codigo_cidade INTEGER,
    tipo_nfe VARCHAR,
    data_hora_de_saida varchar,
    data_hora_de_emissao varchar,
    numero_nfe VARCHAR,
    natureza_da_operacao VARCHAR,
    codigo_cnf INTEGER,
    modelo_nfe varchar,
    local_destino VARCHAR,
    serie VARCHAR,
    pedido bigint,
    cpf_cnpj_cliente varchar,
    nome_cliente varchar,
    url_qr_code varchar,
    cnpj varchar,
    nome_empresa varchar,
    endereco varchar,
    data_hora_de_autorizacao varchar,
    troco real,
    valor_pago real,
    tipo_pagamento varchar,
    is_nota_fiscal boolean
);;

GRANT ALL PRIVILEGES ON TABLE venda TO SERRANO;;


CREATE TABLE produto_venda (
    id bigserial PRIMARY KEY,
    unidade_de_medida VARCHAR ,
    quantidade real,
    valor real,
    porcentagem_desconto real,
    valor_total real,
    nome VARCHAR,
    ncm INTEGER,
    ean INTEGER,
    cest INTEGER,
    escala_relevante BOOLEAN,
    cnpj_fabricante VARCHAR,
    nome_fabricante VARCHAR,
    origem varchar,
    tributacao varchar,
    csosn varchar,
    compoe_nota BOOLEAN,
    cfop VARCHAR,
    id_venda bigint,
    id_produto bigint,
    valor_desconto real
);;
GRANT ALL PRIVILEGES ON TABLE produto_venda TO SERRANO;;
	


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;



INSERT  INTO  RELATORIO(nome,descricao,is_resource ,visibilidade)
VALUES('checkout/Nfce.jrxml','Cupom fiscal',true,'READ_ONLY');;

INSERT  INTO RELATORIO_PARAMETRO(NOME,DESCRICAO,ID_RELATORIO,VISIBILIDADE,is_file)
VALUES('QR_CODE_PARAM','InputStream contendo o qr code da url de consulta ',(SELECT last_value FROM relatorio_id_seq),'READ_ONLY',true);;

INSERT  INTO RELATORIO_PARAMETRO(NOME,DESCRICAO,ID_RELATORIO,VISIBILIDADE,is_file)
VALUES('VENDA_PARAM','Venda do produto',(SELECT last_value FROM relatorio_id_seq),'READ_ONLY',false);;


INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('configuracao.log','Exibir Logs');;


    GRANT USAGE,
SELECT
  ON ALL SEQUENCES IN SCHEMA public to SERRANO;;



INSERT INTO PERMISSAO(NOME,DESCRICAO) VALUES('configuracao.whatsapp','Exibir Configuração Whatsapp');;




