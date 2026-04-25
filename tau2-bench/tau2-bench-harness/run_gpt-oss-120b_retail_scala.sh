MODEL=openai/gpt-oss-120b
DOMAIN=retail
uv run tau2 run \
  --scala-mode \
  --agent llm_agent \
  --domain $DOMAIN \
  --agent-llm openrouter/$MODEL \
  --user-llm openrouter/$MODEL \
  --num-trials 10 --save-to gpt-oss-120b_"$DOMAIN"_scala --max-concurrency 10
