import tkinter as tk
from tkinter import messagebox
from componentes import Accumulator, CodeEditor, CommandRegister, DataCell, grid_components

class AccumulatorProgram:
    def __init__(self, root):
        self.cells = [DataCell(root, cell_id) for cell_id in range(20)]
        self.accumulator = Accumulator(root)
        self.code_editor = CodeEditor(root)
        self.command_register = CommandRegister(root)

        self.run_button = tk.Button(root, text="Run Code", command=self.run_code)
        self.step_button = tk.Button(root, text="Step", command=self.step)
        self.reset_button = tk.Button(root, text="Reset", command=self.resetAll)

        grid_components(self)
        
        self.resetAll()

    def run_code(self):
        self.resetAll()
        self.done = False

        # code_lines = self.code_editor.text_field.get("1.0", tk.END).splitlines()
        
        while not self.done:
            self.step()
        
        # while int(self.command_register.value) < len(code_lines) + 1:
        #     new_instruction_pointer  = int(self.execute_line(code_lines[self.command_register.value-1], self.command_register.value))
            
        #     if new_instruction_pointer == -1:
        #         messagebox.showerror("Error", f"Invalid command in line {self.command_register.value}")
        #         break
        #     if new_instruction_pointer == -2:
        #         messagebox.showerror("Error", f"Infinite loop detected at line {self.command_register.value}")
        #         break
        #     if new_instruction_pointer == int(self.command_register.value):
        #         self.command_register.value = new_instruction_pointer + 1
        #         self.command_register.update()
        #         messagebox.showinfo("Info", f"Program execution completed at line {self.command_register.value}")
        #         self.done = True
        #         break
            
        #     self.command_register.value = new_instruction_pointer 
        #     self.command_register.update()
    
    def step(self):
        code_lines = self.code_editor.text_field.get("1.0", tk.END).splitlines()
        
        if int(self.command_register.value) > len(code_lines) or self.done:
            self.resetAll()
            self.done = False
        
        new_instruction_pointer  = int(self.execute_line(code_lines[self.command_register.value-1], self.command_register.value))
            
        if new_instruction_pointer == -1:
            messagebox.showerror("Error", f"Invalid command in line {self.command_register.value}")
            return
        if new_instruction_pointer == -2:
            messagebox.showerror("Error", f"Infinite loop detected at line {self.command_register.value}")
            return
        if new_instruction_pointer == int(self.command_register.value):
            self.command_register.value = new_instruction_pointer + 1
            self.command_register.update()
            messagebox.showinfo("Info", f"Program execution completed at line {self.command_register.value}")
            self.done = True
            return
        
        self.command_register.value = new_instruction_pointer 
        self.command_register.update()
    
    def execute_line(self, line, instruction_pointer) -> int:
        tokens = line.split()
        
        # if (len(tokens) <= 0):
        if not tokens:
            return instruction_pointer + 1
        elif (len(tokens) <= 1):
            if tokens[0] == 'END':
                return instruction_pointer
            return instruction_pointer + 1
        
        command = tokens[0]
        arguments = tokens[1:]
        
        if command == 'DLOAD':
            self.dload_value(arguments[0])
        elif command == 'STORE':
            self.store_value(arguments[0])
        elif command == 'LOAD':
            self.load_value(arguments[0])
        elif command == 'ADD':
            self.add_value(arguments[0])
        elif command == 'SUB':
            self.subtract_value(arguments[0])
        elif command == 'MULT':
            self.multiply_value(arguments[0])
        elif command == 'DIV':
            self.divide_value(arguments[0])
        elif command == 'JUMP':
            return self.jump(arguments[0], instruction_pointer)
        else:
            messagebox.showerror("Error", f"Invalid command: {command}, in line {instruction_pointer}")
            return -1
        
        return instruction_pointer + 1
    
    def jump(self, n, befehlsregister):
        print (n)
        print(befehlsregister)
        if int(n) == int(befehlsregister):
            return -2
        return n
        
    def dload_value(self, value):
        try:
            value = int(value)
            self.accumulator.value = value
            self.accumulator.update()
        except ValueError:
            messagebox.showerror("Error", "Invalid value for DLOAD command")

    def load_value(self, value):
        try:
            value = int(value)
            self.accumulator.value = self.cells[value].value
            self.accumulator.update()
        except ValueError:
            messagebox.showerror("Error", "Invalid value for DLOAD command")

    def store_value(self, cell_number):
        try:
            cell_number = int(cell_number)
            if 0 <= cell_number < len(self.cells):
                self.cells[cell_number].value = self.accumulator.value
                self.cells[cell_number].update()
            else:
                messagebox.showerror("Error", f"Invalid cell number: {cell_number}")
        except ValueError:
            messagebox.showerror("Error", "Invalid value for STORE command")

    def add_value(self, cell_number):
        try:
            cell_number = int(cell_number)
            self.accumulator.value += self.cells[cell_number].value
            self.accumulator.update()
        except ValueError:
            messagebox.showerror("Error", "Invalid value for ADD command")

    def subtract_value(self, cell_number):
        try:
            cell_number = int(cell_number)
            self.accumulator.value -= self.cells[cell_number].value
            self.accumulator.update()
        except ValueError:
            messagebox.showerror("Error", "Invalid value for SUBTRACT command")

    def multiply_value(self, cell_number):
        try:
            cell_number = int(cell_number)
            self.accumulator.value *= self.cells[cell_number].value
            self.accumulator.update()
        except ValueError:
            messagebox.showerror("Error", "Invalid value for MULTIPLY command")

    def divide_value(self, cell_number):
        try:
            cell_number = int(cell_number)
            if self.cells[cell_number].value != 0:
                self.accumulator.value //= self.cells[cell_number].value
                self.accumulator.update()
            else:
                messagebox.showerror("Error", "Division by zero")
        except ValueError:
            messagebox.showerror("Error", "Invalid value for DIVIDE command")

    def resetAll(self):
        self.accumulator.reset()
        self.command_register.reset()
        for cell in self.cells:
            cell.reset()

if __name__ == "__main__":
    root = tk.Tk()
    root.title("Accumulator Program")
    app = AccumulatorProgram(root)
    root.mainloop()