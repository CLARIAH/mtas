# Corpus Query Language

Within Lucene and Solr, each field containing tokenized text can be considered as a set of tokens, where each token is associated with a position and its value can be seen as a word from the original text. Mtas extends this concept by allowing to associate multiple positions with one token and by associating each token with a prefix and optional postfix instead of the single value. This makes it possible to use multiple tokens on the same position, and distinguish annotations by using a unique prefix for each type, and allows structures like sentences, paragraphs or entities consisting of multiple adjacent or non-adjacent positions.

To describe sets of tokens matching some condition, a query language is needed. Mtas supports CQL based on the Corpus Query Language introduced by the [Corpus WorkBench](http://cwb.sourceforge.net/files/CQP_Tutorial/) and supported by the Lexicom [Sketch Engine](http://www.sketchengine.co.uk/documentation/wiki/SkE/CorpusQuerying).

<a name="prefix"></a>

#### Prefix

For each field containing Mtas tokenized text, every token is associated with a prefix. Within the field, only a limited set of prefixes is used to distinguish between the different types of annotation. By using a [prefix query](search_query_prefix.html) a full list of used prefixes can be produced.

<a name="value"></a>

#### Value

The optional postfix associated with a token can be queried within CQL by providing a *value*. This is a regular expression, the supported syntax is documented in the RegExp class provided by Lucene. By using a [termvector query](search_query_termvector.html), for each [prefix](#prefix) a list of postfix values can be produced. 

<a name="#cql"></a>

## CQL

| Syntax                                | Description                      | Example      |
|---------------------------------------|----------------------------------|--------------|
| [token](#token)                       | Matches a single position token  | `[t=\"de\"]` |
| [multi-position](#multi-position)     | Matches a (single or) multi position token   | `<s/>`       |
| [sequence](#sequence)                 | Matches a sequence               | `[pos="ADJ"]{2}[pos="N"]` |

| Syntax                                | Description                      | Example      |
|---------------------------------------|----------------------------------|--------------|
| [cql](#cql)**{** \<number\> **}**     | Matches provided number of occurrence from [cql](#cql)| `[pos=\"ADJ\"]{2}` |
| [cql](#cql)**{** \<number\> , \<number\>**}** | Matches each number between provided start and end of occurrence from [cql](#cql)| `[pos=\"ADJ\"]{2,3}` |



| Syntax                                | Description                                     | Example |
|---------------------------------------|-------------------------------------------------|---------|
| **\(** [cql](#cql) **\) within \(** [cql](#cql) **\)**  | Matches cql expression within another cql expression   | `([t=\"de\"]) within (<s/>)` |
| **\(** [cql](#cql) **\) !within \(** [cql](#cql) **\)**  | Matches cql expression not within another cql expression   | `([t=\"de\"]) !within (<s/>)` |
| **\(** [cql](#cql) **\) containing \(** [cql](#cql) **\)**  | Matches cql expression containing another cql expression   | `(<s/>) containing ([t=\"de\"])` |
| **\(** [cql](#cql) **\) !containing \(** [cql](#cql) **\)**  | Matches cql expression not containing another cql expression   | `(<s/>) !containing ([t=\"de\"])` |

<a name="#token"></a>

## Token

| Syntax                              | Description                                     | Example |
|-------------------------------------|-------------------------------------------------|---------|
| **\[ \]**                               | Matches each single position token | `[]` |
| **\[** [single-position-expression](#single-position-expression) **\]**  | Matches single position token with condition defined by an [single-position-expression](#single-position-expression)   | `[t="de"]` |

<a name="single-position-expression"></a>

#### Single Position Expression

| Expression  | Syntax                                      | Example |
|-------------|---------------------------------------------|---------|
| basic       | [prefix](#prefix) **= \"**[value](#value)**\"** | `t="de"`
| not         | **\!** [single-position-expression](#single-position-expression) | `!t="de"` |
| and         | **\(** [single-position-expression](#single-position-expression) **\&** [single-position-expression](#single-position-expression) **\&** ... **\)** | `t="de" & pos="LID"`|
| or          | **\(** [single-position-expression](#single-position-expression) **\|** [single-position-expression](#single-position-expression) **\|** ... **\)** | `t="de" | t="het"` |
| position    | **\#** \<position\> | `#100` |
| range       | **\#** \<position\> **-** \<position\>   | `#100-110` |


<a name="#multi-position"></a>

## Multi-position

| Syntax                              | Description                                     | Example |
|-------------------------------------|-------------------------------------------------|---------|
| **\<** [multi-position-expression](#multi-position-expression) **/\>**  | Matches (single and) multi position tokens with condition defined by [multi-position-expression](#multi-position-expression)   | `<s/>` |
| **\<** [multi-position-expression](#multi-position-expression) **\>**  | Matches start of (single and) multi position tokens with condition defined by [multi-position-expression](#multi-position-expression)   | `<s>` |
| **\</** [multi-position-expression](#multi-position-expression) **\>**  | Matches end of (single and) multi position tokens with condition defined by [multi-position-expression](#multi-position-expression)   | `</s>` |


<a name="multi-position-expression"></a>

#### Multi Position Expression

| Expression  | Syntax                                            |
|-------------|---------------------------------------------------|
| prefix      | [prefix](#prefix)                                 |
| basic       | [prefix](#prefix) **= \"**[value](#value)**\"** |


<a name="#sequence"></a>

## Sequence

| Syntax                                | Description                      | Example      |
|---------------------------------------|----------------------------------|--------------|
| [cql](#cql)  [cql](#cql)  [cql](#cql)... | A sequence of [cql](#cql)  | `[t="de"][pos=\"ADJ\"]{2}[pos="N"]` |

