# Algoritmo de Difusão Atômica Rápido à Despeito de Colisões Tolerante a Falhas Bizantinas

## Objetivo
Implementar os algoritmos CFABCast, BCFABCast e USIG-BCFABCast.  
Para ver o paper que discute os algoritmos [clique aqui](https://sbrc2017.ufpa.br/downloads/trilha-principal/ST05_01.pdf)

## Modo de Implementação
Cada algoritmo é depende do anterior, então deve ser feito 
primeiro o CFABCast para depois implementar o  BCFABCast e por ùltimo o USIG-BCFABCast.


### Detalhes
- NONE = isEmpty();
- ⊥(Absurdo) = null;

O arquivo currentView deve ser gerado pelo sistema ao iniciar (Ele deve ser apagado e gerado novamente se for atualizado o numero de instancias (file: system.config => property:system.initial.view)).
