set terminal epslatex
set output 'packetlossline.eps'
set datafile separator ","

set xrange [10:60]
set xtics 10 nomirror tc lt 1
set xlabel 'Packet loss rate (\%)' tc lt 1

set yrange [1000:1300]
set ytics 100 nomirror tc lt 1
set ylabel 'Latency (ms)' tc lt 1

plot 'packetlossline.csv' using 1:2 title 'Client-server' with line lt 1, 'packetlossline.csv' using 1:3 title 'Peer-to-peer' with line lt 2