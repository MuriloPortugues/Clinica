//caminho padrão para imagens
:imagesdir: images
:figure-caption: Figura
:doctype: book

//gera apresentacao
//pode se baixar os arquivos e add no diretório
:revealjsdir: https://cdnjs.cloudflare.com/ajax/libs/reveal.js/3.8.0

//GERAR ARQUIVOS
//make slides
//make ebook

//Estilo do Sumário
:toc2: 
//após os : insere o texto que deseja ser visível
:toc-title: Sumário
:figure-caption: Figura
//numerar titulos
:numbered:
:source-highlighter: highlightjs
:icons: font
:chapter-label:
:doctype: book
:lang: pt-BR
//3+| mesclar linha tabela

= Programação para Web II
Fagno Alves Fonseca <fagno.fonseca@ifto.edu.br>
Mestre em Modelagem Computacional de Sistemas – UFT.

1. link:introducao/[Introdução ao Desenvolvimento Web]
1. link:ambiente-desenvolvimento/[Preparação do Ambiente de Desenvolvimento]
1. link:spring/[Spring]
1. link:jpa/[JPA - Java Persistence API]
1. link:validacao/[Validação com Bean Validation]
1. link:security/[Spring Security]

//IMPORTANT: MATERIAL EM CONSTRUÇÃO

// .Teste [fonte: referência]
// image::cliente-faz.png[] 

//== GIF
//image::horizontal-scaling-small.gif[]
//incluir link dentro do colchetes caso desejar [link=http://google.com]

//== VIDEO
//video::a4HCgieO3Kc[youtube,width=640,height=480]


// == Código fonte
// .Título Código
// [source, java]
// ----
// public class Pessoa{

//     private String nome;
//     private int idade;

//     public String getNome(){
//         return nome;
//     }

//     public int getIdade(){
//         return idade;
//     }

// }

// == Código fonte (incluir código fonte)
// .Título Código
// [source, java]
// ----
// include::Teclado.java[]
// ----


// == Código fonte (Trecho do código fonte)
// .Título Código
// [source, java]
// ----
// //add a tag dentro do código ( tag::main[] e finalizar ( end::main[] ) )
// include::Teclado.java[tag=main]
// ----

// == Link para outro README 
// link:comandos/[Lista de Comandos]
