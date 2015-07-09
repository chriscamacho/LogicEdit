LogicEdit

a logic network editor, saves to and load XML "circuits"

use ant to build and run - built and tested using java 8

select the gate type you want to add using the dropdown at the top right of the UI

click the "new gate" button - the aproprate gate will be created in the centre of the view

to connect an output (on the right of gates) to an input (on the left of gates) hover over an output till its highlighted, click, hover over the input until it is higlighted, click, they should be connected with a wire

to delete a wire double click on the output its connected to

to delete a gate, click on it then press the delete key

to change the state of an "IN" gate click on its indicator to toggle its state if its bright then the output is high...

save, new and load are self explanatory 

the main "grid" can be panned by clicking and dragging the mouse

with the initial release the following circuits can be loaded (as well as your own creations that you save)

first.xml          first ever working circuit with this project, just showing the outputs of a few gates
test.xml           simpler that first just testing a splitter!
xor.xml            an xor gate made from NOT AND and OR gates
add-nibbles.xml    adds two nibbles (vertically low to high, top to bottom) with carry
feedback.xml       experiment into handling feedback loops



initially the following logic plugins are available

And      logic AND two inputs one output
In       user changable input with one output
Not      logic NOT one input and one output
Or       logic OR two inputs one output
Out      shows visually the state of its one input
Split    provides two output identical to its one input
Xor      logic XOR two inputs and one output


BUGS

after loading you cant change the size of the window by draging, maximising and
windowising using the window decorations still works as expected
