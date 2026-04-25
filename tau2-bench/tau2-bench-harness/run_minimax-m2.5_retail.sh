MODEL=minimax/minimax-m2.5
DOMAIN=retail
uv run tau2 run \
  --agent llm_agent \
  --domain $DOMAIN \
  --agent-llm openrouter/$MODEL \
  --user-llm openrouter/$MODEL \
  --num-trials 10 --save-to minimax-m2.5_"$DOMAIN" --max-concurrency 10
