# Assignment 4. Patient Zero

Student ID: 253949   
Name: Maksat Nauanuly   
Group: SE - 2512

This file is the report. How to start: `START_HERE.md`. The task: `docs/Assignment4.pdf`.

## Program output

The 9 lines my program prints on my data (`java -cp out Main`):

    Mutations: 107
    Shared core: 974
    Marker hits: 6
    First hit: 5036
    Border length: 4
    Shared words: 310
    Tests needed: 35
    Cheapest testing: 140
    Longest chain: 25

<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/fe18cce6-f834-479b-9ae9-4abcd935cefe" />



## Questions

Mission 1. What does the cell d[i][j] of your table mean, and how is it computed from other cells?

d[i][j] is the smallest number of changes (replace, insert, delete) that turn the first i letters of A into the first j letters of B. The first row is 0..m (insert j letters) and the first column is 0..n (delete i letters). For other cells I take the minimum of three ways: the diagonal cell d[i-1][j-1] plus 0 if the letters are equal or plus 1 if not (replace), the top cell d[i-1][j] plus 1 (delete), and the left cell d[i][j-1] plus 1 (insert). The answer is the bottom right cell d[n][m].

Mission 2. How is the longest common subsequence different from the longest common substring? My Shared core is bigger than the number of letters that are the same at the same place: why?

A substring must be continuous, a subsequence can skip letters but keeps the order. In my LCS table L[i][j], if the letters are equal I take the diagonal plus 1, otherwise the bigger of the top and the left cell. My Shared core (974) is bigger than the number of equal letters at the same positions, because after an insertion or a deletion the two strains shift against each other, and letters can still be matched with a shift when the order is kept.

Mission 3. What does the prefix function tell you, and why does the pointer in the text never go back in KMP? What is the Border length of my marker and what does it mean?

pi[i] is the length of the longest proper prefix of p[0..i] that is also a suffix of it (a border). In KMP, k is the number of marker letters matched. On a mismatch I do not move the text pointer back: the last k text letters are equal to the first k marker letters, so the table pi[k-1] already tells me how many of them still match, and I only change k. So the text pointer i only goes forward and the search takes O(n + m). My marker is ACCTCCGACCT and its Border length is 4: the prefix ACCT is also the suffix ACCT. It means that two occurrences of the marker can overlap by 4 letters, and after a full match KMP continues with k = 4, not 0.

Mission 4. Why must you compare the letters after the hashes are equal? What happens to the cost of Rabin-Karp if many windows have the same hash?

Two different words can have the same hash (a collision), because there are more words than hash values. Equal hashes only say that the words may be equal, so I compare the letters before I count. If many windows have the same hash, I compare letters many times, and the time goes from O(n + m) on average toward O(n * m * k) in the worst case.

Mission 5. What are the two states of a person in your DP, and how do you get the answer for a person from its children? Why does a greedy "test the person with most links first" not always give the cheapest answer?

For every person v I keep in[v], the cheapest cost of its subtree when v is tested, and out[v], the cheapest cost when v is not tested. in[v] = cost[v] + sum over children c of min(in[c], out[c]). out[v] = sum over children c of in[c], because if v is not tested then every child must be tested to cover the link. The answer is min(in[0], out[0]) at the root, and I rebuild who is tested by walking down from the root. Greedy takes the person with most links, but it ignores the costs, and even with equal costs it can be wrong: for example, a person with many links can have a very expensive test, while testing his cheap neighbours covers the same links for less. The DP checks both choices for every person, so it is always correct.

## AI use

I used Claude (Anthropic) as a helper. It wrote the first version of the code for the missions (edit distance, LCS, prefix function and KMP, Rabin-Karp, tree DP), added the comments, and helped me to run the project in IntelliJ and to prepare this README. I read the code, ran `java tools/Check.java` and the program on my data, and I studied each algorithm to be able to explain it in the quiz.

## Timing (Mission 7)

Milliseconds, average of 3 runs, from `java -cp out Bench`.

| test | size | time |
|---|---|---|
| naive search | 20 000 / 40 000 / 80 000 | 62 / 108 / 216 |
| KMP | 20 000 / 40 000 / 80 000 | 2.5 / 1.4 / 0.4 |
| edit distance | 500 / 1000 / 2000 / 4000 | 6.1 / 6.8 / 24 / 99 |

When the text becomes twice as long, the naive search takes about twice as much time (62, 108, 216 ms), because it compares the long pattern at every position (O(n * m)). KMP stays at a few milliseconds, because the text pointer never goes back and every letter is read once (O(n + m)); the tiny numbers are only measurement noise. The edit distance grows about 4 times when the length doubles (2000 to 4000: 24 to 99 ms), because the table has n * m cells, so the time is quadratic.

## Complexity

n is the length of the text (or of a strain), m the length of the pattern, k the length of the window, v the number of people.

| algorithm | time | space | assumptions |
|---|---|---|---|
| Edit distance | O(n * m) | O(n * m) | The table has (n+1)(m+1) cells, each one is computed in O(1) from three neighbours. Two rows would be enough if only the number is needed. |
| LCS | O(n * m) | O(n * m) | The same table as the edit distance, each cell in O(1). |
| Prefix function and KMP | O(n + m) | O(m) | The pointer k goes up by 1 at most once per step, so the total number of rollbacks is at most n + m. The prefix table has m numbers. |
| Rabin-Karp (shared words) | O(n + m) on average, O(n * k) in the worst case | O(m) | The rolling hash is updated in O(1). The worst case is when many hashes collide and the letters are compared every time. The hashes of the windows of B are stored in a map. |
| Tree DP | O(v) | O(v) | Every person and every link is visited once (BFS order, then two passes). |

