module WordCounter (
  Word,
  Count,
  WordFreq,
  countWords,
  main
) where

import Control.Applicative ((<$>))
import Data.Char (toLower)
import Data.List (group, sort)
import System.Environment (getArgs)

type Word = String
type Count = Int
-- | 単語の出現回数
data WordFreq = WordFreq Word Count deriving (Eq, Show)
instance Ord WordFreq where
  -- カウントの降順、単語の昇順
  compare (WordFreq wrd1 cnt1) (WordFreq wrd2 cnt2)
    | cnt1 /= cnt2  = compare cnt2 cnt1
    | otherwise     = compare wrd1 wrd2

toWordFreq :: [Word] -> WordFreq
toWordFreq ws = WordFreq (head ws) (length ws)

isWordElem :: Char -> Bool
isWordElem = (`notElem` ",.;:?!/'\"()[]")

-- 文字列を単語のリストに編集する。
wordList :: String -> [Word]
wordList = words . map toLower . filter isWordElem

-- | 欧文の文字列に含まれる単語の出現回数をカウントする。
--   大文字と小文字は区別せず、記号類は除外する。
countWords :: String -> [WordFreq]
countWords = sort . map toWordFreq . group . sort . wordList

main :: IO ()
main = do
  args <- getArgs
  if length args /= 1
    then putStrLn "Please specify an input file."
    else do
      let (f:_) = args
      countWords <$> readFile f >>= print
