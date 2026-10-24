UI_CONTEXT.md — WALNUS SMARTVIEW PRO

UI Design System, Reference Website Audit & Animation Specification

Project: Walnus SmartView Pro
Reference website: https://winzaro3.com
Platform: Android
Primary interface: Native Android UI, with web-based animation support only where a WebView is intentionally used
Animation engine: Anime.js
Implementation method: Modular, theme-driven, device-adaptive
Status: Design specification — reference-specific visual details require verification before being declared exact.

---

1. Design Objective

Build a premium, modern, technology-driven interface for SmartView Pro that combines the visual polish of a contemporary digital product with the practical requirements of a professional building-survey application.

Use the reference website to guide a visual audit of layout, typography, colour, imagery, iconography, interaction patterns, and animation.

Do not copy the reference website's business content, branding, assets, or proprietary source code. Extract and document its observable design patterns, then adapt appropriate patterns to SmartView Pro.

The resulting interface must communicate:

- Professional painting and construction expertise.
- Accuracy and reliability.
- Modern AI-assisted functionality.
- Clear navigation and efficient workflows.
- Premium visual quality without unnecessary complexity.
- Accessibility across a broad Android device ecosystem.

2. Reference Website Audit

Before implementing the reference-inspired UI, inspect https://winzaro3.com using an accessible browser and record the following.

Design element| Required audit
Layout| Header, navigation, content width, spacing, card arrangement and page structure.
Typography| Actual font family, available weights, font sizes, line heights and letter spacing.
Icons| Library or icon font, SVG assets, stroke weight, fill style and standard icon dimensions.
Colours| Sampled background, surface, text, primary, secondary, accent, border and gradient colours.
Images| File formats, aspect ratios, image treatments, overlays, cropping and compression.
Components| Buttons, cards, navigation elements, forms, dialogs, badges and interactive controls.
Motion| Entrance effects, hover states, transitions, loading indicators and scroll-triggered animations.
Responsiveness| Layout changes across mobile, tablet and desktop viewports.

Record verified findings separately from inferred design choices.

Do not invent exact CSS values, font names, icon libraries, or animation timings. If an element cannot be verified, label it "UNVERIFIED" and choose an explicit SmartView Pro design default.

Reference inspection is a design-research step, not permission to copy the site's assets or execute its scripts inside the Android application.

3. Visual Design Direction

Adopt a premium, technology-focused aesthetic with clean layouts, strong hierarchy, restrained visual effects, and purposeful animation.

The UI should combine:

- Clear, high-contrast typography.
- Consistent spacing and alignment.
- Carefully structured cards and panels.
- Rounded corners used consistently.
- Subtle borders and elevation.
- Restrained gradients and translucent surfaces where appropriate.
- Crisp icons and high-quality project imagery.
- Smooth transitions that reinforce the user's actions.

Avoid excessive glass effects, decorative animations, unnecessary gradients, and visual clutter that would reduce readability during real construction-site work.

The application must remain a practical productivity tool rather than becoming an entertainment-style interface.

4. Application Theme System

Implement a central theme system that allows users to select their preferred application appearance.

Theme selection must update all supported screens consistently without changing business logic or requiring a restart unless the Android architecture makes an immediate update impractical.

Theme A — SmartView Signature Dark

Purpose: Default premium technology interface.

Token| Colour
Background| "#0B0F14"
Surface| "#141B24"
Elevated surface| "#1C2633"
Primary accent| "#35C6F4"
Secondary accent| "#7C5CFF"
Primary text| "#F4F7FB"
Secondary text| "#A7B4C4"
Border| "#2B3948"
Success| "#35C98A"
Warning| "#F5B942"
Error| "#FF646E"

Use for dashboards, project overviews, AI processing panels, advanced visualisation, and premium product experiences.

Theme B — Professional Light

Purpose: Daylight usability and conventional business workflows.

Token| Colour
Background| "#F5F7FA"
Surface| "#FFFFFF"
Elevated surface| "#FFFFFF"
Primary accent| "#087EA4"
Secondary accent| "#6254C7"
Primary text| "#17212B"
Secondary text| "#526171"
Border| "#D9E1E8"
Success| "#18794E"
Warning| "#946200"
Error| "#B42332"

Use for reports, data entry, quotations, measurement tables, and prolonged daytime use.

Theme C — Construction High Visibility

Purpose: High-visibility interaction for fieldwork.

Token| Colour
Background| "#101214"
Surface| "#1B2024"
Elevated surface| "#252C31"
Primary accent| "#FFD43B"
Secondary accent| "#FF7A24"
Primary text| "#FFFFFF"
Secondary text| "#D0D7DE"
Border| "#46515A"
Success| "#44D17A"
Warning| "#FFD43B"
Error| "#FF6262"

Use for camera overlays, site surveys, measurement feedback, and high-visibility controls.

Colour must never be the only way to communicate status. Include labels, icons, and other accessible indicators.

Theme D — Calm Blue

Purpose: A restrained, professional alternative.

Token| Colour
Background| "#EDF3F8"
Surface| "#FFFFFF"
Elevated surface| "#F7FAFD"
Primary accent| "#2563EB"
Secondary accent| "#0891B2"
Primary text| "#172554"
Secondary text| "#475569"
Border| "#CBD5E1"
Success| "#15803D"
Warning| "#A16207"
Error| "#B91C1C"

Theme requirements

- Provide a theme selector in application settings.
- Include a system-default option that follows Android light/dark mode.
- Persist the user's chosen theme.
- Use semantic colour tokens rather than hard-coded colours throughout individual screens.
- Apply themes consistently to dialogs, navigation, cards, forms, reports, buttons and supported animation overlays.
- Verify text contrast and focus visibility.
- Keep construction-status colours semantically consistent across themes.

The four named palettes are SmartView Pro design proposals, not verified colours extracted from Winzaro3.

5. Typography

The reference website's actual typeface must be verified during the visual audit.

Until verified, use Inter as the proposed application typeface, with Android system sans-serif as the fallback.

Recommended hierarchy:

Role| Proposed size| Weight
Main screen title| 28 sp| Bold
Section heading| 22 sp| Semibold
Card heading| 18 sp| Semibold
Body text| 16 sp| Normal
Supporting text| 14 sp| Normal
Labels| 12–14 sp| Medium
Primary button| 16 sp| Semibold
Measurement value| 24–32 sp| Bold

These are initial design values, not extracted website specifications.

Use scalable Android text units, support system font scaling, and avoid clipping on smaller screens.

Use tabular numerals where appropriate for dimensions, quantities, prices, and measurements.

6. Icon Library

Inspect the reference website to establish its actual icon library before describing that library as part of the exact reference specification.

For SmartView Pro, use Material Symbols as the proposed default icon system for native Android screens, unless the existing project already uses a consistent icon library.

Requirements:

- Use one consistent visual style wherever possible.
- Use filled icons for selected navigation states where appropriate.
- Use outlined icons for secondary actions.
- Keep icon dimensions consistent within equivalent controls.
- Provide content descriptions for meaningful icons.
- Use vector assets where practical.
- Avoid emoji as substitutes for production interface icons.
- Do not introduce multiple icon libraries without a documented reason.

Suggested functional icons include:

- Projects
- Camera and capture
- Survey and measurements
- Paint and colour selection
- Plastering and screeding
- Materials and quotations
- AI assistance
- Reports
- Settings
- Subscription and account

Use familiar icon conventions for essential actions.

7. Image Formats and Asset Management

Audit the reference website's image formats and optimisation strategy before recording its exact implementation.

For SmartView Pro, adopt the following asset policy:

Asset| Preferred format
App launcher icon| Android adaptive icon resources
Simple interface illustrations| SVG source converted to Android-compatible vectors where appropriate
Photographs and site captures| JPEG
Transparency-required graphics| PNG or WebP
Optimised photographic assets| WebP where supported and appropriate
Animated web illustrations| CSS/SVG animation or WebP where justified
Small UI icons| Vector assets

Native Android interfaces should use Android-compatible resources rather than assuming browser image handling.

For captured site imagery:

- Preserve the original when required by the survey workflow.
- Generate appropriately sized previews and thumbnails.
- Avoid loading full-resolution images unnecessarily.
- Handle image orientation and metadata correctly.
- Respect user permissions and privacy.
- Avoid unnecessary copies of large files.

Use actual building, surface, paint, and construction imagery relevant to the user's task. Decorative assets must not obscure survey controls or measurement results.

8. Component Architecture

Build the interface as reusable, independently maintainable components.

Required component groups include:

Application shell

- App header
- Navigation
- Screen container
- Theme selector
- Shared dialog and bottom-sheet patterns

Project management

- Project card
- Survey card
- Project status indicator
- Recent activity item
- Empty-state component

Survey tools

- Camera capture control
- Survey information panel
- Measurement display
- Surface-selection control
- Colour-selection panel
- Quantity summary

Commercial tools

- Material estimate card
- Supplier information row
- Quotation summary
- Report preview
- Premium-feature indicator

Feedback and AI

- Progress indicator
- Loading state
- Success and error feedback
- AI suggestion panel
- Animated data reveal
- Offline or connectivity status

Each component must support the central theme system, clear accessibility semantics, and appropriate loading, empty, error, and disabled states.

Do not make a component dependent on animation for its underlying functionality.

9. Anime.js Animation System

Important architectural distinction

Anime.js is a JavaScript animation engine. It does not provide a native Android component library.

The current SmartView Pro repository uses Android technologies. Therefore, Anime.js must not be imported into Kotlin or Jetpack Compose screens as if it were a native Android animation API.

Implement Anime.js only inside an explicitly defined web-based UI module or WebView.

For native Android screens, use Jetpack Compose animation APIs or the appropriate Android animation framework.

The interface should use reusable, Anime.js-powered web components wherever a web-based module is appropriate. Avoid duplicating the same visual animation logic across unrelated screens.

Required animation patterns

Use Anime.js to implement the following patterns in supported web-based modules:

1. Entrance animations
   
   - Fade and translate cards into view.
   - Reveal section headings and supporting content.
   - Stagger related elements where this improves visual hierarchy.

2. Interactive feedback
   
   - Animate button and card states.
   - Provide smooth selection feedback.
   - Use restrained transitions for tabs and panels.

3. Survey HUD
   
   - Animate scanning indicators.
   - Provide subtle targeting and alignment feedback.
   - Animate selection outlines without obscuring captured imagery.

4. Colour visualisation
   
   - Animate transitions between selected colour previews.
   - Provide surface-selection feedback.
   - Keep colour changes responsive and clearly identifiable.

5. AI processing
   
   - Display indeterminate progress when completion time is unknown.
   - Animate completed results into view.
   - Use real application state rather than fabricated progress percentages.

6. Reports and statistics
   
   - Reveal figures after actual results become available.
   - Animate progress indicators using real data.
   - Preserve readable values throughout transitions.

7. Navigation
   
   - Use short, restrained transitions between web-based panels.
   - Avoid animations that delay access to essential controls.

Animation defaults

The following values are proposed starting points and must be adjusted after device testing.

Animation| Initial target
Button feedback| 100–160 ms
Small component transition| 160–220 ms
Card entrance| 220–320 ms
Panel transition| 200–300 ms
Decorative background motion| Optional; low frequency

Use restrained easing and avoid excessive bouncing, overshooting, or long delays.

Implementation rules

- Centralise reusable animation definitions and configuration.
- Use supported Anime.js APIs appropriate to the installed version.
- Pin the dependency version and document it.
- Prefer transform and opacity animations for routine UI motion.
- Clean up animation instances and event listeners when components are removed.
- Prevent duplicate animations from accumulating during repeated navigation.
- Avoid continuous animation while a screen is inactive.
- Respect reduced-motion preferences.
- Provide static alternatives when animations are disabled or unsupported.
- Do not animate critical measurement calculations or alter the underlying values.
- Do not delay user actions while decorative animation is running.

Primary rule: Use Anime.js as the principal animation engine for the web-based UI components that adopt this system, while retaining native Android animation tools for native screens.

10. Low-Spec Device Performance

SmartView Pro must remain usable on lower-spec Android devices.

The animation system must support three operating profiles:

- Enhanced: Additional visual transitions for capable devices.
- Standard: Essential component animations with restrained effects.
- Reduced motion: Minimal or no nonessential animation.

The application must be able to reduce animation intensity independently of its business logic.

Avoid large numbers of simultaneously animated DOM elements, expensive blur effects, oversized background videos, and unnecessary WebView rendering.

Do not introduce a WebView solely to animate a screen that can be implemented efficiently using native Android components.

Test memory consumption, frame stability, navigation responsiveness, and camera interactions on representative low- and high-spec devices.

11. Responsive Layout and Accessibility

Design for small phones first, then enhance the layout for larger phones and tablets.

Requirements:

- Adapt to available width and height.
- Support system font scaling.
- Keep primary controls easy to reach.
- Avoid horizontal scrolling for essential workflows.
- Provide visible feedback for actions.
- Use accessible labels and touch targets.
- Preserve keyboard and accessibility focus.
- Respect system reduced-motion preferences.
- Maintain sufficient contrast in every theme.
- Avoid making essential information dependent on colour or animation.

The application must remain usable with animations disabled.

12. Theme and Animation Preferences

Provide a settings section for appearance preferences.

Required options:

- System default
- SmartView Signature Dark
- Professional Light
- Construction High Visibility
- Calm Blue

Provide an animation preference with:

- Automatic
- Standard
- Reduced motion

Use the device's capabilities and accessibility preferences to determine an appropriate default.

Persist settings using the existing application architecture. Theme selection must not interfere with project records, survey data, camera functionality, or subscription entitlements.

13. Implementation Workflow

Before implementation:

1. Read "README.md", "ARCHITECTURE.md", "AI_WORKFLOW.md", and "PROGRESS_TRACKER.md".
2. Inspect the existing UI framework and identify native versus web-based screens.
3. Audit the reference website and document verified design findings.
4. Identify existing theme, typography, icon, and component implementations.
5. Define module boundaries and acceptance criteria.

During implementation:

1. Establish semantic design tokens.
2. Implement the theme system.
3. Standardise typography and icons.
4. Build reusable components.
5. Introduce animation patterns only in appropriate rendering environments.
6. Implement reduced-motion and low-spec fallbacks.
7. Add tests and update documentation.

After implementation:

1. Run relevant tests and static analysis.
2. Build a fresh APK before the qualifying commit.
3. Test navigation, theme selection, and critical workflows.
4. Verify camera and survey operations remain functional.
5. Test representative low- and high-spec devices.
6. Document any features that remain unverified.
7. Produce the required SV completion report.

14. Acceptance Criteria

- [ ] Reference website audited and findings documented.
- [ ] Exact website font and icon library verified or explicitly marked unverified.
- [ ] Image formats and colour values verified or explicitly marked unverified.
- [ ] Four selectable application themes specified and implemented as authorised.
- [ ] Theme changes propagate consistently.
- [ ] Reusable components follow the established architecture.
- [ ] Anime.js is isolated to appropriate web-based modules.
- [ ] Native Android screens use native animation frameworks.
- [ ] Reduced-motion support is available.
- [ ] Low-spec fallbacks are implemented.
- [ ] Animations do not block essential user interactions.
- [ ] Existing camera, survey, persistence, and quotation functionality is protected.
- [ ] Tests and APK build are verified.
- [ ] Documentation and completion report are updated.

15. Final Directive

Create a premium, consistent, modular SmartView Pro interface inspired by verified observations of the reference website, not invented assumptions.

Treat the theme system, typography, icons, image assets, reusable components, and animation behavior as coordinated parts of a single UI design system.

Use Anime.js extensively and consistently for supported web-based components, but do not compromise the native Android architecture to force its use where it does not belong.

The final result must be visually polished, accessible, responsive, performant, and reliable for real construction-site work.

Design quality is mandatory. Functional reliability is non-negotiable.

------------------------------------------------------------------
16. IMPLEMENTATION STATUS (SV-001)
------------------------------------------------------------------

No user interface exists yet. This document therefore describes the required design
system, not delivered screens. The following status table must be maintained so the
design specification is never mistaken for implemented UI.

| Area | Status | Notes |
|---|---|---|
| Design tokens (colour, spacing, type) | NOT IMPLEMENTED | Arrives with `:core:designsystem` in SV-002 |
| SmartView Signature Dark | NOT IMPLEMENTED | Target default theme |
| Professional Light | NOT IMPLEMENTED | |
| Construction High Visibility | NOT IMPLEMENTED | |
| Calm Blue | NOT IMPLEMENTED | |
| System default following OS setting | NOT IMPLEMENTED | |
| Reusable components (cards, chips, empty/error states) | NOT IMPLEMENTED | |
| Anime.js web modules | NOT IMPLEMENTED | Remains restricted to web-based UI; no WebView is planned for native screens |
| Reduced-motion support | NOT IMPLEMENTED | Requirement retained for SV-002 |
| Accessibility (touch targets, scaling, contrast) | NOT IMPLEMENTED | Requirement retained for SV-002 |

Decisions already binding on the future UI:

1. The estimator screen must show provenance labels next to every figure, and must not
   present derived litres as a measurement (see `Provenance` in `:core:model`).
2. Premium states come from `EntitlementEvaluator` decisions; the UI renders
   `Allowed` / `UpgradeRequired` / `VerificationRequired` / `ProjectLimitReached` and never
   inspects plan names itself.
3. Assumption text from `PaintEstimate.assumptions` must remain visible on the result
   screen and in any exported quotation.
4. Themes must be implemented as token sets so the estimator and project screens survive
   a redesign unchanged.
