# Gnuplot script

set autoscale
unset log
unset label

set xlabel "Test #"
set ylabel "# Pass/Fail test"
set title "# Tests"

set term png
set output "sum.png"

set style data lines

set style fill pattern 2

plot 'sum.dat' using 1:4:5 w filledcu notitle \
     , ''      using 1:2:4 w filledcu notitle

