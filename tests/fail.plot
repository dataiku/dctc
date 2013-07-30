# Gnuplot script

set autoscale
unset log
unset label

set xlabel "Test #"
set ylabel "# Fail"
set title "# Fail"
set logscale y

set term png
set output "fail.png"

set style data lines

set style fill pattern 2

plot 'sum.dat' using 1:4:5 w filledcu notitle
