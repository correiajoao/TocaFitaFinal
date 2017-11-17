Aluno: João Lucas Marques Correia 

Aluno: Leandro martins de Freitas

## Toca Fita
Toca Fita é uma aplicação de áudio, assim, o usuário conectado a essa aplicação pode escutar suas músicas favoritas, escutar os seus streams favoritos e até mesmos criar os seus próprios streams para que outros usuários possam escutar. O aplicativo funciona apenas em sistemas UNIX. A aplicação possui dois módulos principais, um de servidor, que é escalável e aceita conexão com diversos clientes, e um de cliente híbrido, que hora é cliente, hora é servidor.

Devemos iniciar o módulo de servidor para que seja possível conectar os clientes. Com o servidor já iniciado, podemos iniciar quantos clientes quisermos (fez-se uso de multi-threads para que isso seja possível). Cada cliente que se conecta ao servidor, pode acessar três funcionalidades principais, explanadas nos próximos tópicos.

#### Criar Stream
Nesta configuração, o cliente funciona como um agente híbrido, primeiro solicitando ao servidor o cadastro de um novo stream, (isso significa que o cliente irá iniciar um servidor de stream e gostaria que o servidor global armazenasse suas configurações de conexão) e em seguida iniciando um servidor de stream de áudio.

Na fase de cadastro de um novo stream, o cliente submete ao servidor uma requisição para cadastro e informa o nome da stream. Após isso, o servidor escreve o IP de origem do stream e a Porta de origem do stream. Após o servidor global sinalizar o armazenamento das configurações, o cliente inicia o seu servidor de stream. 
O servidor de stream, assim como o servidor global suporta múltiplas conexões, através do uso de threads. Quando o servidor de stream está ativo, o cliente não pode realizar atividade com o servidor global, estando dedicado exclusivamente à sua função de servidor.

#### Requisitar lista de streams e ouvir streams
Escolhendo essa opção, o cliente pode verificar a lista de streams ativos conhecidos pelo servidor, então através do código do stream, pode solicitar dados de conexão a um stream em específico. O servidor retorna esses dados, que são: IP de origem do servidor de stream e Porta de origem do servidor de stream. Munido desses dados, o cliente agora pode realizar uma conexão direta ao servidor de stream e escutar a sua transmissão.

Caso não haja streams ativos, essa opção retornará uma mensagem, avisando ao cliente que ainda não existem streams ativos. Caso contrário, a lista de streams ativas no momento é mostrada cada uma com seu identificador e nome. Escolhendo o identificador da stream, o cliente entra na sala e a escuta.

É importante notar que, devido ao uso de multi-thread, uma stream pode ter diversos ouvintes, cada um recebendo dados do proprietário da stream.

#### Requisitar lista de músicas e ouvir músicas
Escolhendo essa opção, uma lista de músicas (pré-existentes no servidor) é apresentada ao cliente, cada uma com nome e código identificador. Basta apenas informar o código da música desejada e a música será transmitida do servidor ao cliente.

Caso o servidor não possua músicas, essa opção retornará uma mensagem indicando que não existem músicas no servidor.

## Funcionalidades não implementadas
Não há como visualizar o número de ouvintes de uma stream em andamento. O controle de músicas (play, pause, etc.) não foi implementado.

Após um servidor de stream encerrar a sua transmissão, o servidor não retira os dados do mesmo da sua lista de streams ativos.

## Dificuldades encontradas
Tentamos implementar o stream de música utilizando o protocolo UDP, porém se mostrou um pouco complicado, devido a sua diferença de paradigma e modo de implementação. Essa atividade mesclada com a arquitetura híbrida dificultou a implementação, assim optamos por manter todas as conexões utilizando TCP.

Um outro problema foi a manipulação das conexões, quando fechar as conexões, quando as manter ativas.

A definição do protocolo da camada de aplicação também é um pouco complicada, porém tentamos utilizar um pouco da nossa experiência anterior para contornar o problema, determinamos tipos específicos de mensagens e funções específicas para tratamento das mensagens trocadas por nosso protocolo. 
