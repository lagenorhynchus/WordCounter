#load "str.cma"

module WordCounter : sig
  type word_freq = {
    word : string;
    count : int
  }
  val string_of_word_freq : word_freq -> string
  val count_words : string -> word_freq list
end = struct
  (* 単語の出現回数 *)
  type word_freq = {
    word : string;
    count : int
  }

  let string_of_word_freq wf =
    Printf.sprintf "{word = %S; count = %d}" wf.word wf.count

  (* カウントの降順、単語の昇順 *)
  let word_freq_comparator wf1 wf2 =
    if wf1.count <> wf2.count
      then compare wf2.count wf1.count
      else compare wf1.word wf2.word

  let to_word_freq ws =
    {word = List.hd ws; count = List.length ws}

  let filter_word_elem =
    Str.global_replace (Str.regexp "[,.;:?!/'\"()\\[]") ""

  (* 文字列を単語のリストに編集する。 *)
  let word_list s =
    s |> filter_word_elem |> String.lowercase |> Str.split (Str.regexp "[ \t\r\n]+")

  let group_by f l =
    let rec group acc = function
    | [] -> acc
    | (x :: _) as l ->
      let l1, l2 = List.partition (f x) l in
      group (l1 :: acc) l2 in
    group [] l

  (*
   * 欧文の文字列に含まれる単語の出現回数をカウントする。
   * 大文字と小文字は区別せず、記号類は除外する。
   *)
  let count_words s =
    s |> word_list |> group_by (fun x y -> x = y) |> List.map to_word_freq
      |> List.sort word_freq_comparator
end

let () =
  if Array.length Sys.argv <> 2
    then print_endline "Please specify an input file."
    else
      let f_stream = Stream.of_channel (open_in Sys.argv.(1)) in
      let list_of_stream stream =
        let result = ref [] in
        Stream.iter (fun value -> result := value :: !result) stream;
        List.rev !result in
      let implode l =
        let result = String.create (List.length l) in
        let rec impl i = function
        | [] -> result
        | c :: l ->
          result.[i] <- c;
          impl (i + 1) l in
        impl 0 l in
      f_stream |> list_of_stream |> implode |> WordCounter.count_words
        |> List.iter (fun wf -> print_endline (WordCounter.string_of_word_freq wf))
