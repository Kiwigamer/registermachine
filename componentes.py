import tkinter as tk
from tkinter import messagebox

def grid_components(self):
    self.code_editor.grid(row=0, column=0, columnspan=2)
    self.run_button.grid(row=1, column=0)
    self.step_button.grid(row=1, column=1)
    self.reset_button.grid(row=2, column=1)
    self.accumulator.label.grid(row=0, column=2)
    self.accumulator.value_label.grid(row=0, column=3)
    self.command_register.label.grid(row=2, column=0)
    self.command_register.value_label.grid(row=3, column=0)

    for i, cell in enumerate(self.cells):
        cell.grid(row=i+1, column=2)

class DataCell:
    def __init__(self, root, cell_id):
        self.cell_id = cell_id
        self.value = 0
        self.label = tk.Label(root, text=f"Cell {self.cell_id}:")
        self.value_label = tk.Label(root, text=self.value)

    def update(self): 
        self.value_label.config(text=self.value)

    def reset(self):
        self.value = 0
        self.update()

    def grid(self, row, column, **kwargs):
        self.label.grid(row=row, column=column, **kwargs)
        self.value_label.grid(row=row, column=column+1, **kwargs)

class Accumulator:
    def __init__(self, root):
        self.value = 0
        self.label = tk.Label(root, text=f"Accumulator:")
        self.value_label = tk.Label(root, text=self.value)

    def update(self):
        self.value_label.config(text=self.value)
        
    def reset(self):
        self.value = 0
        self.update()
        
class CommandRegister:
    def __init__(self, root):
        self.value = 1
        self.label = tk.Label(root, text=f"Befehlsregiaster:")
        self.value_label = tk.Label(root, text=self.value)

    def update(self):
        self.value_label.config(text=self.value)

    def reset(self):
        self.value = 1
        self.update()

class CodeEditor:
    def __init__(self, root):
        self.text_field = tk.Text(root, wrap=tk.WORD, width=40, height=15)
        self.text_field.tag_configure("highlight", background="yellow")

    def grid(self, row, column, **kwargs):
        self.text_field.grid(row=row, column=column, **kwargs)
