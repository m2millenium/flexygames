---
version: alpha
name: FlexyGames
description: Modern, clean UI for match/session details with team compositions, scores, and participation history
colors:
  primary: "#667eea"
  secondary: "#764ba2"
  accent: "#f093fb"
  success: "#28a745"
  warning: "#ffc107"
  danger: "#dc3545"
  gray: "#f8f9fa"
  dark: "#1a1a2e"
  background: "#ffffff"
  surface: "#ffffff"
  border: "#e9ecef"
typography:
  h1:
    fontFamily: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
    fontSize: 28px
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "-0.02em"
  h2:
    fontFamily: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
    fontSize: 22px
    fontWeight: 600
    lineHeight: 1.25
    letterSpacing: "-0.01em"
  h3:
    fontFamily: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
    fontSize: 18px
    fontWeight: 600
    lineHeight: 1.3
    letterSpacing: normal
  body-md:
    fontFamily: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
    fontSize: 14px
    fontWeight: 400
    lineHeight: 1.5
  code:
    fontFamily: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace
    fontSize: 12px
    fontWeight: 400
rounded:
  sm: 6px
  md: 12px
  lg: 16px
  xl: 24px
spacing:
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
elevation:
  card:
    shadow: "0 4px 20px rgba(0,0,0,0.08)"
    borderRadius: "{rounded.md}"
  dialog:
    shadow: "0 20px 60px rgba(0,0,0,0.15)"
    borderRadius: "{rounded.lg}"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "#ffffff"
    rounded: "{rounded.sm}"
    padding: "12px 24px"
    fontWeight: 600
    border: "none"
    boxShadow: "0 2px 8px rgba(102,126,234,0.3)"
  button-primary-hover:
    backgroundColor: "{colors.secondary}"
    boxShadow: "0 4px 12px rgba(118,75,162,0.4)"
  button-secondary:
    backgroundColor: "{colors.background}"
    textColor: "{colors.primary}"
    rounded: "{rounded.sm}"
    padding: "12px 24px"
    fontWeight: 600
    border: "2px solid {colors.border}"
  button-danger:
    backgroundColor: "{colors.danger}"
    textColor: "#ffffff"
    rounded: "{rounded.sm}"
    padding: "12px 24px"
    fontWeight: 600
    border: "none"
  card:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.lg}"
    padding: "24px"
    shadow: "{elevation.card.shadow}"
  session-title:
    fontFamily: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
    fontSize: 28px
    fontWeight: 700
    color: "{colors.dark}"
  session-meta:
    fontFamily: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
    fontSize: 14px
    fontWeight: 400
    color: "#6c757d"
  score-display:
    fontFamily: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
    fontSize: 48px
    fontWeight: 300
    color: "{colors.primary}"
  team-label:
    fontFamily: Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
    fontSize: 16px
    fontWeight: 600
    color: "{colors.dark}"
  stat-card:
    backgroundColor: "{colors.gray}"
    rounded: "{rounded.md}"
    padding: "16px"
    boxShadow: "{elevation.card.shadow}"
  table-header:
    backgroundColor: "{colors.primary}"
    textColor: "#ffffff"
    padding: "12px 16px"
    fontWeight: 600
  table-row:
    backgroundColor: "{colors.background}"
    textColor: "{colors.dark}"
    padding: "12px 16px"
    borderBottom: "1px solid {colors.border}"
---

## Overview

FlexyGames implements a modern, clean interface for tracking match sessions with team compositions, scores, and participation history. The design prioritizes clarity, quick information retrieval, and intuitive navigation.

### Design Goals

- **Readability**: Large, legible typography with appropriate contrast
- **Visual hierarchy**: Clear organization of information through spacing and size
- **Actionability**: Prominent buttons for session management actions
- **Responsive**: Clean grid layouts that adapt to different screen sizes

## Colors

### Primary Palette

| Token | Value | Usage |
|-------|-------|-------|
| `primary` (#667eea) | Main brand color for primary actions and highlights |
| `secondary` (#764ba2) | Hover states and supporting elements |
| `accent` (#f093fb) | Highlights and special attention items |

### Functional Colors

| Token | Value | Usage |
|-------|-------|-------|
| `success` (#28a745) | Confirmed actions and positive metrics |
| `warning` (#ffc107) | Attention needed items |
| `danger` (#dc3545) | Destructive actions and error states |

### UI Colors

| Token | Value | Usage |
|-------|-------|-------|
| `background` (#ffffff) | Page background |
| `surface` (#ffffff) | Card and component backgrounds |
| `border` (#e9ecef) | Dividers and borders |
| `dark` (#1a1a2e) | Primary text and headings |
| `gray` (#f8f9fa) | Subtle backgrounds for secondary info |

## Typography

### Font Stack

```
Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif
```

### Scale

| Token | Size | Weight | Usage |
|-------|------|--------|-------|
| `h1` | 28px | 700 | Page title |
| `h2` | 22px | 600 | Section headings |
| `h3` | 18px | 600 | Subsection headings |
| `body-md` | 14px | 400 | Body text |
| `code` | 12px | 400 | Inline code |

## Layout & Spacing

### Section Spacing

| Token | Value | Usage |
|-------|-------|-------|
| `sm` | 8px | Tight spacing within components |
| `md` | 16px | Standard component padding |
| `lg` | 24px | Section gaps and large padding |
| `xl` | 32px | Major section divisions |

### Border Radius

| Token | Value | Usage |
|-------|-------|-------|
| `sm` | 6px | Buttons, small cards |
| `md` | 12px | Standard cards |
| `lg` | 16px | Modal dialogs, panels |
| `xl` | 24px | Full-page containers |

## Components

### Session Header

- **Title**: Large, bold (28px, 700) in dark color
- **Meta info**: Smaller (14px), gray text below title
- **Style**: Clean layout with appropriate spacing

### Score Summary Card

- **Background**: Gradient (primary to secondary)
- **Score display**: Large (48px), light font, white text
- **Team labels**: Bold, semi-transparent white
- **Padding**: Generous (32px vertical, 24px horizontal)

### Team Composition Cards

- **Style**: White card with subtle shadow
- **Layout**: Grid (2 columns) for team A vs team B
- **Player list**: Clean bullet points with player names
- **Highlight**: Current/session player marked subtly

### Round Tabs

- **Active state**: Primary color background, white text
- **Inactive state**: Gray background, dark text
- **Shape**: Rounded pills with appropriate padding

### Round Scores Display

- **Layout**: 3-column grid (Team A | Comparison | Team B)
- **Stat cards**: Gray background with subtle shadow
- **Team comparison**: Green for higher score, red for lower

### Action Buttons

| State | Background | Text | Border |
|-------|------------|------|--------|
| Primary | `#667eea` | `#ffffff` | None |
| Primary Hover | `#764ba2` | `#ffffff` | None |
| Secondary | `#ffffff` | `#1a1a2e` | `#e9ecef` |
| Danger | `#dc3545` | `#ffffff` | None |

## Do's and Don'ts

### Do's

- Use primary color for primary actions (add round, manage session)
- Use consistent spacing (md/lg) between sections
- Use cards with subtle shadows for content grouping
- Use large, legible typography for scores and key metrics
- Apply subtle gradients to highlight important sections

### Don'ts

- Don't use more than 3 font weights (400, 600, 700)
- Don't reduce spacing below `sm` (8px) between elements
- Don't use pure black (#000000) - use dark gray instead
- Don't overload rounds sections with too much data
- Don't use flashy animations or transitions
