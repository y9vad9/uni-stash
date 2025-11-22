CXX = g++
CXXFLAGS = -std=c++17 -Wall -Wextra

LAB1_SRC = lab1/lab1_yaroshchuk_pi_22_1.cpp
LAB1_BIN = build/lab1

lab1: $(LAB1_BIN)

$(LAB1_BIN): $(LAB1_SRC) | build_dir
	$(CXX) $(CXXFLAGS) -o $(LAB1_BIN) $(LAB1_SRC)

build_dir:
	mkdir -p build

clean:
	rm -rf build