import { render, screen } from "@testing-library/react"
import { Button } from "../Button"
import { expect, test } from "vitest"

test("renders button with text", () => {
  render(<Button>Click me</Button>)
  const buttonElement = screen.getByText(/Click me/i)
  expect(buttonElement).toBeInTheDocument()
})
